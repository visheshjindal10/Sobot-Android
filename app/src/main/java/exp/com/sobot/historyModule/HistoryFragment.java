package exp.com.sobot.historyModule;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import exp.com.sobot.Models.Note;
import exp.com.sobot.R;
import exp.com.sobot.historyModule.database.NotesDatabaseHelper;


public class HistoryFragment extends android.app.Fragment {

    private RecyclerView rvNotes;
    private List<Note> noteList = new ArrayList<>();


    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotesDatabaseHelper database = new NotesDatabaseHelper(getActivity());
        Cursor cursor = database.getAll();
        if (cursor != null){
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                noteList.add(new Note(cursor.getString(1),cursor.getString(2)));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes_list, container, false);
        rvNotes = (RecyclerView) view.findViewById(R.id.rvNotes);
        LinearLayoutManager layoutManger = new LinearLayoutManager(getActivity());
        NotesAdapter notesAdapter = new NotesAdapter(noteList);
        layoutManger.setReverseLayout(true);
        layoutManger.setStackFromEnd(true);
        rvNotes.setLayoutManager(layoutManger);
        rvNotes.setAdapter(notesAdapter);
        return view ;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
