package saugatchetry.com.chitchat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private View groupFragmentView;

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> groupList = new ArrayList<>();

    private DatabaseReference rootReference;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("GroupFragments","On Create Called");
        groupFragmentView =  inflater.inflate(R.layout.fragment_groups, container, false);

        rootReference = FirebaseDatabase.getInstance().getReference().child("Groups");

        initializeUIComponents();

        getGroupNamesFromBackend();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String currentGroupNameClicked = adapterView.getItemAtPosition(position).toString();
                redirectToGroupChat(currentGroupNameClicked);
            }
        });

        return groupFragmentView;
    }

    private void redirectToGroupChat(String currentGroupNameClicked) {

        Intent groupChatIntent = new Intent(getContext(),GroupChatActivity.class);
        groupChatIntent.putExtra("groupName",currentGroupNameClicked);
        startActivity(groupChatIntent);
    }

    private void getGroupNamesFromBackend() {

        rootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();

                while(iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }

                groupList.clear();
                groupList.addAll(set);
                Log.d("GroupFragments",""+groupList.size());
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initializeUIComponents() {

        listView = (ListView) groupFragmentView.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, groupList);
        listView.setAdapter(arrayAdapter);


    }

}
