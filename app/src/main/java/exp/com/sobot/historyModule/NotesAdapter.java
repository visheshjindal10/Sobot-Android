package exp.com.sobot.historyModule;


import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import exp.com.sobot.Models.Note;
import exp.com.sobot.R;

public class NotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Note> noteList = new ArrayList<>();

    public NotesAdapter(List<Note> noteList) {
        this.noteList = noteList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notes,parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Note note = noteList.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        if (!TextUtils.isEmpty(note.getTimeStamp()))viewHolder.tvTimeStamp.setText(note.getTimeStamp());
        if (!TextUtils.isEmpty(note.getNote()))viewHolder.tvNote.setText(note.getNote());
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder{
        private AppCompatTextView tvNote;
        private AppCompatTextView tvTimeStamp;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNote = (AppCompatTextView) itemView.findViewById(R.id.tvNotes);
            tvTimeStamp = (AppCompatTextView) itemView.findViewById(R.id.tvTimeStamp);
        }
    }
}
