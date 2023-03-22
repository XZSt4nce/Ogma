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
import android.widget.TextView;

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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SheduleFragment extends Fragment {

    private FragmentSheduleBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SheduleViewModel sheduleViewModel =
                new ViewModelProvider(this).get(SheduleViewModel.class);

        new MyTask().execute();
        binding = FragmentSheduleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        sheduleViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
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
            Map<String,String> preps = new HashMap<>();
            Map<String,String> grps = new HashMap<>();
            int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
            String semestr;
            Map<String, String> data = new LoggedInUserView().getData();
            if (month >= 9) semestr = "osen";
            else semestr = "vesna";

            String prepod_id = "";
            String group_id = "";

            try {
                url = new URL("https://e-spo.ru/org/rasp/export/site/index?pid=1");
                doc = Jsoup.parse(url, 3000);
                Element prepods = doc.getElementById("raspbasesearch-prepod_id");
                Element groups = doc.getElementById("raspbasesearch-group_id");
                for (Element p : prepods.getElementsByTag("option")) {
                    if (p.html() != "Преподаватель" && p.html() != "---")
                        preps.put(p.html(), p.attr("value"));
                }
                for (Element g: groups.getElementsByTag("option")) {
                    if (g.html() != "Группа")
                        grps.put(g.html(), g.attr("value"));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (Objects.equals(data.get("role"), "curator")) {
                try {
                    String fio = new String(format("%s %s %s", data.get("lastName"), data.get("name"), data.get("middleName")).getBytes("ISO-8859-1"), "UTF-8");
                    url = new URL("https://e-spo.ru/org/rasp/export/site/index?pid=1&RaspBaseSearch%5Bgroup_id%5D=&RaspBaseSearch%5Bsemestr%5D=" + semestr + "&RaspBaseSearch%5Bprepod_id%5D=" + preps.get(fio));
                    doc = Jsoup.parse(url, 3000);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                try {
                    String group = data.get("group");
                    url = new URL("https://e-spo.ru/org/rasp/export/site/index?pid=1&RaspBaseSearch%5Bgroup_id%5D=" + grps.get(group) + "44&RaspBaseSearch%5Bsemestr%5D=" + semestr + "&RaspBaseSearch%5Bprepod_id%5D=");
                    doc = Jsoup.parse(url, 3000);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            Elements newsHeadlines = doc.getElementsByClass("card h-100");
            for (Element headline : newsHeadlines) {
                Log.d("FFFF", headline.html());
            }
            return "Done";
        }


        @Override
        protected void onPostExecute(String result) {
            // ignore
        }
    }
}