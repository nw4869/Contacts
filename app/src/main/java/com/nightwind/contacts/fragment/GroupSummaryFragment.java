package com.nightwind.contacts.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.app.Fragment;
import android.os.RemoteException;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nightwind.contacts.R;
import com.nightwind.contacts.activity.GroupMembersActivity;
import com.nightwind.contacts.activity.MainToolbarActivity;
import com.nightwind.contacts.model.Contacts;
import com.nightwind.contacts.model.GroupSummary;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupSummaryFragment extends MainToolbarActivity.PlaceholderFragment {

    RecyclerView recyclerView;
    private ArrayList<GroupSummary> groups;
    private GroupSummaryAdapter adapter;
    private View emptyView;

    public GroupSummaryFragment() {
        // Required empty public constructor
    }

    public static GroupSummaryFragment newInstance() {
        return new GroupSummaryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_group_summary, container, false);
        emptyView = v.findViewById(R.id.emptyView);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        groups = new ArrayList<>();
        adapter = new GroupSummaryAdapter(getActivity(), groups);
        recyclerView.setAdapter(adapter);

        loadData();

        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_group, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_group) {
            final EditText editText = new EditText(getActivity());
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setView(editText)
                    .setTitle(R.string.action_add_group)
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String groupTitle = String.valueOf(editText.getText());
                            long id = new Contacts(getActivity()).addGroup(groupTitle);
                            groups.add(new GroupSummary(id, groupTitle, 0));
                            adapter.notifyItemInserted(groups.size());
                            refreshEmpty();
                        }
                    }).create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void reloadData() {
        super.reloadData();
        loadData();
    }

    private void loadData() {
        getLoaderManager().restartLoader(0, null, new LoaderManager.LoaderCallbacks<List<GroupSummary>>() {
            @Override
            public Loader<List<GroupSummary>> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<List<GroupSummary>>(getActivity()) {
                    @Override
                    public List<GroupSummary> loadInBackground() {
                        return new Contacts(getContext()).getGroupSummary();
                    }

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        forceLoad();
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<List<GroupSummary>> loader, List<GroupSummary> data) {
                groups.clear();
                groups.addAll(data);
                refreshUI();
            }

            @Override
            public void onLoaderReset(Loader<List<GroupSummary>> loader) {
            }
        });
    }

    private void refreshUI() {
        adapter.notifyDataSetChanged();
        refreshEmpty();
    }

    private void refreshEmpty() {
        if (adapter.getItemCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    private class GroupSummaryViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView count;

        public GroupSummaryViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            count = (TextView) itemView.findViewById(R.id.count);
        }

    }

    private class GroupSummaryAdapter extends RecyclerView.Adapter<GroupSummaryViewHolder> {

        private final Context context;
        private final List<GroupSummary> groups;

        public GroupSummaryAdapter(Context context, List<GroupSummary> groups) {
            super();
            this.context = context;
            this.groups = groups;
        }

        @Override
        public GroupSummaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_group_summary, parent, false);
            return new GroupSummaryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(GroupSummaryViewHolder holder, final int position) {
            final GroupSummary group = groups.get(position);
            holder.title.setText(group.getTitle());
            holder.count.setText(group.getCount() + "人");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), GroupMembersActivity.class);
                    intent.putExtra(GroupMembersActivity.ARG_GROUP_ID, group.getId());
                    startActivity(intent);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Dialog dialog = new AlertDialog.Builder(getActivity()).setTitle(R.string.group_operate)
                            .setNegativeButton("修改名称", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    final EditText editText = new EditText(getActivity());
                                    editText.setText(group.getTitle());
                                    new AlertDialog.Builder(getActivity())
                                            .setView(editText)
                                            .setTitle(R.string.action_edit_group)
                                            .setNegativeButton("取消", null)
                                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String groupTitle = String.valueOf(editText.getText());
                                                    int toastRes;
                                                    if (new Contacts(getActivity()).updateGroup(group.getId(), groupTitle)) {
                                                        toastRes = R.string.edit_success;
                                                        group.setTitle(groupTitle);
                                                        adapter.notifyDataSetChanged();
                                                    } else {
                                                        toastRes = R.string.edit_failed;
                                                    }

                                                    Toast.makeText(getActivity(), toastRes, Toast.LENGTH_SHORT).show();

                                                }
                                            }).show();
                                }
                            })
                            .setPositiveButton("删除分组", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new AlertDialog.Builder(getActivity()).setTitle(R.string.warn_delete_group)
                                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    try {
                                                        new Contacts(getActivity()).deleteGroup(group.getId());
                                                        Toast.makeText(getActivity(), R.string.delete_success, Toast.LENGTH_SHORT).show();
                                                    } catch (RemoteException | OperationApplicationException e) {
                                                        e.printStackTrace();
                                                        Toast.makeText(getActivity(), R.string.delete_failed, Toast.LENGTH_SHORT).show();
                                                    }
                                                    groups.remove(position);
                                                    adapter.notifyItemRemoved(position);
                                                    refreshEmpty();
                                                }
                                            })
                                            .setNegativeButton("取消", null)
                                            .show();
                                }
                            })
                            .create();
                    dialog.show();
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return GroupSummaryFragment.this.groups.size();
        }
    }
}
