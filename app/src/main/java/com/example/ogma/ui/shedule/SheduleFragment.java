package com.example.ogma.ui.shedule;

import static java.lang.String.format;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ogma.databinding.FragmentSheduleBinding;
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

    private FragmentSheduleBinding binding;
    public static String[] header;
    public static Object[] dates = new Object[6];
    public static Map<String,String> preps = new HashMap<>();
    public static Map<String,String> grps = new HashMap<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SheduleViewModel sheduleViewModel =
                new ViewModelProvider(this).get(SheduleViewModel.class);

        new MyTask().execute();
        binding = FragmentSheduleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TableLayout table = binding.sheduleTable;
        //sheduleViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("StaticFieldLeak")
    private static class MyTask extends AsyncTask<Void, Void, String> {

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
                doc = Jsoup.parse(url, 3000);
                Element prepods = doc.getElementById("raspbasesearch-prepod_id");
                Element groups = doc.getElementById("raspbasesearch-group_id");
                for (Element p: prepods.getElementsByTag("option")) {
                    if (!p.html().equals("Преподаватель") && !p.html().equals("---"))
                        preps.put(p.html(), p.attr("value"));
                }
                for (Element g: groups.getElementsByTag("option")) {
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
                    doc = Jsoup.parse(url, 3000);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Elements days = doc.getElementsByClass("card h-100");
                int ptr_d = 0;
                for (Element day: days) {
                    String weekday = day.getElementsByClass("card-title").text();
                    Elements rows = day.getElementsByTag("tr");
                    Object[] lessons = new Object[10];
                    int ptr_l = 0;
                    for (Element lesson: rows) {
                        try {
                            String number = lesson.getElementsByClass("pl-2").text();
                            String[] sg = lesson.getElementsByClass("pl-0 pr-0").html().split("<br>");
                            if (sg[0].equals("")) continue;
                            String classroom = "";
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
                            lessons[ptr_l++] = new Object[] {number, subject, classroom, group};
                        } catch (Exception e) {
                            Log.d("lesson_exception", e.toString());
                        }
                    }
                    dates[ptr_d++] = new Object[] { weekday, lessons };
                }
            }
            else {
                header = new String[] { "#", "Дисциплина", "Кабинет", "Преподаватель" };
                try {
                    String group = new String(data.get("group").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    url = new URL("https://e-spo.ru/org/rasp/export/site/index?pid=1&RaspBaseSearch%5Bgroup_id%5D=" + grps.get(group) + "&RaspBaseSearch%5Bsemestr%5D=" + semestr + "&RaspBaseSearch%5Bprepod_id%5D=");
                    doc = Jsoup.parse(url, 3000);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Elements days = doc.getElementsByClass("card h-100");
                int ptr_d = 0;
                for (Element day: days) {
                    String weekday = day.getElementsByClass("card-title").text();
                    Elements rows = day.getElementsByTag("tr");
                    Object[] lessons = new Object[10];
                    int ptr_l = 0;
                    for (Element lesson: rows) {
                        try {
                            String number = lesson.getElementsByClass("pl-2").text();
                            String subject = lesson.getElementsByClass("pl-0 pr-0").html().split("<br>")[0];
                            if (subject.equals("")) continue;
                            String[] classroom = new String[2], teacher = new String[2];
                            Elements cr1 = lesson.getElementsByClass("align-middle text-center pr-0"), cr2 = new Elements();
                            Elements t1 = lesson.getElementsByClass("pl-1"), t2 = new Elements();
                            for (Element t: t1) {
                                t2 = t.getElementsByTag("div");
                            }
                            for (Element cr: cr1) {
                                cr2 = cr.getElementsByTag("div");
                            }
                            if (cr2.size() == 0) {
                                lessons[ptr_l++] = new Object[] {number, subject, cr1.text(), t1.text()};
                            }
                            else {
                                classroom[0] = cr2.get(1).text();
                                classroom[1] = cr2.get(2).text();
                                teacher[0] = t2.get(1).text();
                                teacher[1] = t2.get(2).text();
                                lessons[ptr_l++] = new Object[] {number, subject, classroom, teacher};
                            }
                        } catch (Exception e) {
                            Log.d("lesson_exception", e.getMessage());
                        }
                    }
                    dates[ptr_d++] = new Object[] { weekday, lessons };
                }
            }
            return "Done";
        }


        @Override
        protected void onPostExecute(String result) {
            // ignore
        }
    }
}