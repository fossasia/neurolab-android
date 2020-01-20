package io.neurolab.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import io.neurolab.R;

public class FAQActivity extends AppCompatActivity {

    private String[] questions;
    private String[][] answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        questions = getResources().getStringArray(R.array.faq_questions);

        String[] ans = getResources().getStringArray(R.array.faq_answers);
        answers = new String[ans.length][];
        for (int i = 0; i < ans.length; i++) {
            answers[i] = new String[]{ans[i]};
        }

        ExpandableListView listView;

        listView = findViewById(R.id.expListView);
        listView.setAdapter(new ExpandableListAdapter(questions, answers));
        listView.setGroupIndicator(null);
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private final LayoutInflater inf;
        private String[] questions;
        private String[][] answers;

        public ExpandableListAdapter(String[] questions, String[][] answers) {
            this.questions = questions;
            this.answers = answers;
            inf = LayoutInflater.from(getApplication());
        }

        @Override
        public int getGroupCount() {
            return questions.length;
        }

        @Override
        public int getChildrenCount(int questionPosition) {
            return answers[questionPosition].length;
        }

        @Override
        public Object getGroup(int questionPosition) {
            return questions[questionPosition];
        }

        @Override
        public Object getChild(int questionPosition, int answerPosition) {
            return answers[questionPosition][answerPosition];
        }

        @Override
        public long getGroupId(int questionPosition) {
            return questionPosition;
        }

        @Override
        public long getChildId(int questionPosition, int answerPosition) {
            return answerPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getChildView(int questionPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            ViewHolder holder;
            View v = convertView;
            if (v == null) {
                v = inf.inflate(R.layout.list_item, parent, false);
                holder = new ViewHolder();

                holder.text = (TextView) v.findViewById(R.id.lblListItem);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            holder.text.setClickable(true);
            holder.text.setMovementMethod(LinkMovementMethod.getInstance());
            holder.text.setText(Html.fromHtml(getChild(questionPosition, childPosition).toString()));

            return v;
        }

        @Override
        public View getGroupView(int questionPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View v = convertView;
            if (v == null) {
                v = inf.inflate(R.layout.list_group, parent, false);

                holder = new ViewHolder();
                holder.text = (TextView) v.findViewById(R.id.lblListHeader);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            holder.text.setText(getGroup(questionPosition).toString());

            return v;
        }

        @Override
        public boolean isChildSelectable(int questionPosition, int answerPosition) {
            return true;
        }

        private class ViewHolder {
            private TextView text;
        }
    }
}
