package com.example.ogma.ui.shedule;

import static java.lang.String.format;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.ogma.R;
import com.example.ogma.ui.login.LoggedInUserView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SheduleFragment extends Fragment {
    public static String[] header;
    public static String[][][][] dates = new String[6][2][10][4];
    public static Map<String,String> preps = new HashMap<>();
    public static Map<String,String> grps = new HashMap<>();
    public static boolean notDone = true;

    @RequiresApi(api = Build.VERSION_CODES.P)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Parse().execute();

        while (notDone) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        ScrollView sv = new ScrollView(this.getContext());

        HorizontalScrollView hsv = new HorizontalScrollView(this.getContext());

        TableLayout tableLayout = new TableLayout(this.getContext());
        tableLayout.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT));
        tableLayout.setStretchAllColumns(true);
        tableLayout.setShrinkAllColumns(true);
        tableLayout.setGravity(Gravity.CENTER);

        for(int i = 0; i < 6; i++) {

            int nulls = 0;
            for (int j = 0; j < 10; j++) {
                if (dates[i][1][j][0] == null) nulls++;
            }
            if (nulls == 10) continue;

            TableRow tbrow1 = new TableRow(this.getContext());
            tbrow1.setBackgroundColor(Color.rgb(128, 0, 128));
            tbrow1.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1f));

            TextView tv1 = new TextView(this.getContext());

            tv1.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tv1.setText(dates[i][0][0][0]);
            tv1.setPadding(8, 8, 8, 8);
            tv1.setTextColor(Color.WHITE);
            // tv1.setTextSize(16);
            tbrow1.addView(tv1);
            tableLayout.addView(tbrow1);

            TableRow tbrow2 = new TableRow(this.getContext());
            for (int j = 0; j < 4; j++) {
                TextView tv2 = new TextView(this.getContext());
                tv2.setText(header[j]);
                tv2.setLayoutParams(new TableRow.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                tv2.setPadding(8, 8, 8, 8);
                tv2.setBackgroundResource(R.drawable.border);
                tbrow2.addView(tv2);
            }
            tableLayout.addView(tbrow2);

            int startLesson = 0, endLesson = 10;

            boolean fnp = false;
            for(int j = 0; j < 10; j++) {
                if (dates[i][1][j][0] == null) {
                    if (!fnp) {
                        startLesson = j + 1;
                    }
                } else {
                    endLesson = j + 1;
                    fnp = true;
                }
            }

            for(int j = startLesson; j < endLesson; j++) {
                TableRow tbrow3 = new TableRow(this.getContext());

                TextView number = new TextView(this.getContext());
                number.setText(Integer.toString(j));
                number.setLayoutParams(new TableRow.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                number.setPadding(8, 8, 8, 8);
                number.setBackgroundResource(R.drawable.border);
                tbrow3.addView(number);

                for (int k = 1; k < 4; k++) {
                    TextView tv3 = new TextView(this.getContext());
                    tv3.setText(dates[i][1][j][k]);
                    tv3.setLayoutParams(new TableRow.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    tv3.setPadding(8, 8, 8, 8);
                    tv3.setBackgroundResource(R.drawable.border);
                    tbrow3.addView(tv3);
                }
                tableLayout.addView(tbrow3);
            }
        }
        hsv.addView(tableLayout);
        sv.addView(hsv);


        return sv;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @SuppressLint("StaticFieldLeak")
    private static class Parse extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            URL url;
            Document doc;
            int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
            String semestr;
            Map<String, String> data = new LoggedInUserView().getData();
            if (month >= 9) semestr = "osen";
            else semestr = "vesna";

            try {
                url = new URL("https://e-spo.ru/org/rasp/export/site/index?pid=1");
                doc = Jsoup.parse(url, 0);
                Element prepods = doc.getElementById("raspbasesearch-prepod_id");
                Element groups = doc.getElementById("raspbasesearch-group_id");
                for (Element p: Objects.requireNonNull(prepods).getElementsByTag("option")) {
                    if (!p.html().equals("Преподаватель") && !p.html().equals("---"))
                        preps.put(p.html(), p.attr("value"));
                }
                for (Element g: Objects.requireNonNull(groups).getElementsByTag("option")) {
                    if (!g.html().equals("Группа"))
                        grps.put(g.html(), g.attr("value"));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (Objects.equals(data.get("role"), "curator")) {
                header = new String[] { "#", "Дисциплина", "Кабинет", "Группа" };
                String fio = new String(format("%s %s %s", data.get("lastName"), data.get("name"), data.get("middleName")).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                try {
                    url = new URL("https://e-spo.ru/org/rasp/export/site/index?pid=1&RaspBaseSearch%5Bgroup_id%5D=&RaspBaseSearch%5Bsemestr%5D=" + semestr + "&RaspBaseSearch%5Bprepod_id%5D=" + preps.get(fio));
                    doc = Jsoup.parse(url, 0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Elements days = doc.getElementsByClass("card h-100");
                int ptr_d = 0;
                for (Element day: days) {
                    String[][] weekday = new String[][] { { day.getElementsByClass("card-title").text() }, {}, {}, {}, {}, {}, {}, {}, {}, {} };
                    Elements rows = day.getElementsByTag("tr");
                    String[][] lessons = new String[10][4];
                    int ptr_l = 0;
                    for (Element lesson: rows) {
                        try {
                            String number = lesson.getElementsByClass("pl-2").text();
                            String[] sg = lesson.getElementsByClass("pl-0 pr-0").html().split("<br>");
                            if (sg[0].equals("")) continue;
                            String classroom;
                            String subject = sg[0];
                            String group = sg[1].substring(4);
                            Elements cr1 = lesson.getElementsByClass("align-middle text-center pr-0"), cr2 = new Elements();
                            for (Element cr: cr1) {
                                cr2 = cr.getElementsByTag("div");
                            }
                            if (cr2.size() == 0) {
                                classroom = cr1.text();
                            }
                            else {
                                Elements t = lesson.getElementsByClass("pl-1").get(0).getElementsByTag("div");
                                if (t.get(1).text().equals(fio)) classroom = cr2.get(1).text();
                                else classroom = cr2.get(2).text();
                            }
                            lessons[ptr_l++] = new String[] {number, subject, classroom, group};
                        } catch (Exception e) {
                            Log.d("lesson_exception", e.toString());
                        }
                    }
                    dates[ptr_d++] = new String[][][] { weekday, lessons };
                }
            }
            else {
                header = new String[] { "#", "Дисциплина", "Кабинет", "Преподаватель" };
                try {
                    String group = new String(Objects.requireNonNull(data.get("group")).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    url = new URL("https://e-spo.ru/org/rasp/export/site/index?pid=1&RaspBaseSearch%5Bgroup_id%5D=" + grps.get(group) + "&RaspBaseSearch%5Bsemestr%5D=" + semestr + "&RaspBaseSearch%5Bprepod_id%5D=");
                    doc = Jsoup.parse(url, 0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Elements days = doc.getElementsByClass("card h-100");
                int ptr_d = 0;
                for (Element day: days) {
                    String[][] weekday = new String[][] { { day.getElementsByClass("card-title").text() }, {}, {}, {}, {}, {}, {}, {}, {}, {} };
                    Elements rows = day.getElementsByTag("tr");
                    String[][] lessons = new String[10][4];
                    int ptr_l = 0;
                    for (Element lesson: rows) {
                        try {
                            String number = lesson.getElementsByClass("pl-2").text();
                            String subject = lesson.getElementsByClass("pl-0 pr-0").html().split("<br>")[0];
                            if (subject.equals("")) continue;
                            String classroom, teacher;
                            Elements cr1 = lesson.getElementsByClass("align-middle text-center pr-0"), cr2 = new Elements();
                            Elements t1 = lesson.getElementsByClass("pl-1"), t2 = new Elements();
                            for (Element t: t1) {
                                t2 = t.getElementsByTag("div");
                            }
                            for (Element cr: cr1) {
                                cr2 = cr.getElementsByTag("div");
                            }
                            if (cr2.size() == 0) {
                                lessons[ptr_l++] = new String[] {number, subject, cr1.text(), t1.text()};
                            }
                            else {
                                classroom = cr2.get(1).text() + "\n----------\n" + cr2.get(2).text();
                                teacher = t2.get(1).text() + "\n---------------\n" + t2.get(2).text();
                                lessons[ptr_l++] = new String[] {number, subject, classroom, teacher};
                            }
                        } catch (Exception e) {
                            Log.d("lesson_exception", e.getMessage());
                        }
                    }
                    dates[ptr_d++] = new String[][][] { weekday, lessons };
                }
            }
            notDone = false;
            return "Done";
        }


        @Override
        protected void onPostExecute(String result) {
            // ignore
        }
    }
}