package com.yako.safekids.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.safekids.android.R;
import com.yako.safekids.activity.PhoneNumberList;
import com.yako.safekids.model.Country;

import java.util.List;

/**
 * Created by macbook on 12/05/15.
 */
public class CcPopUpItemAdapter extends RecyclerView.Adapter<CcPopUpItemAdapter.MyViewHolder> {

    public static final String[] countryNames = {"أفغانستان", "ألبانيا",
            "الجزائر", "أندورا", "أنغولا", "أنتاركتيكا", "الأرجنتين",
            "أرمينيا", "اروبا", "أستراليا", "النمسا", "أذربيجان",
            "البحرين", "بنغلاديش", "روسيا البيضاء", "بلجيكا", "بليز", "بنين",
            "بوتان", "بوليفيا", "البوسنة والهرسك", "بوتسوانا",
            "البرازيل", "بروناي دار السلام", "بلغاريا", "بوركينا فاسو",
            "ميانمار", "بوروندي", "كمبوديا", "الكاميرون", "كندا",
            "الرأس الأخضر", "جمهورية أفريقيا الوسطى", "تشاد", "شيلي", "الصين",
            "جزيرة عيد الميلاد", "جزر كوكس", "كولومبيا",
            "جزر القمر", "الكونغو", "جزر كوك", "كوستاريكا", "كرواتيا",
            "كوبا", "قبرص", "جمهورية التشيك", "الدنمارك", "جيبوتي",
            "تيمور الشرقية", "الإكوادور", "مصر", "السلفادور",
            "غينيا الاستوائية", "إريتريا", "استونيا", "إثيوبيا",
            "جزر فوكلاند (مالفيناس)", "جزر فارو", "فيجي", "فنلندا",
            "فرنسا", "بولينيزيا الفرنسية", "الغابون", "غامبيا", "جورجيا",
            "ألمانيا", "غانا", "جبل طارق", "اليونان", "غرينلاند",
            "غواتيمالا", "غينيا", "غينيا بيساو", "غيانا", "هايتي",
            "هندوراس", "هونغ كونغ", "المجر", "الهند", "إندونيسيا", "إيران",
            "العراق", "ايرلندا", "جزيرة آيل أوف مان", "فلسطين", "إيطاليا", "ساحل العاج",
            "جامايكا", "اليابان", "الأردن", "كازاخستان", "كينيا", "كيريباتي",
            "الكويت", "قيرغيزستان", "لاوس", "لاتفيا", "لبنان", "ليسوتو",
            "ليبيريا", "ليبيا", "ليختنشتاين", "ليتوانيا", "لوكسمبورغ",
            "ماكاو", "مقدونيا", "مدغشقر", "ملاوي", "ماليزيا",
            "جزر المالديف", "مالي", "مالطا", "جزر مارشال", "موريتانيا",
            "موريشيوس", "مايوت", "المكسيك", "ميكرونيزيا", "مولدوفا",
            "موناكو", "منغوليا", "الجبل الأسود", "المغرب", "موزمبيق",
            "ناميبيا", "ناورو", "نيبال", "هولندا", "كاليدونيا الجديدة",
            "نيوزيلندا", "نيكاراغوا", "النيجر", "نيجيريا", "نيوي", "كوريا",
            "النرويج", "عمان", "باكستان", "بالاو", "بنما",
            "بابوا غينيا الجديدة", "باراجواي", "بيرو", "الفلبين", "بيتكيرن",
            "بولندا", "البرتغال", "بورتوريكو", "قطر", "رومانيا",
            "الاتحاد الروسي", "رواندا", "سانت بارتيليمي", "ساموا",
            "سان مارينو", "ساو تومي وبرينسيبي", "المملكة العربية السعودية", "السنغال",
            "صربيا", "سيشيل", "سيراليون", "سنغافورة", "سلوفاكيا",
            "سلوفينيا", "جزر سليمان", "الصومال", "جنوب أفريقيا",
            "جمهورية كوريا", "إسبانيا", "سريلانكا", "سانت هيلانة",
            "سان بيار وميكلون", "السودان", "سورينام", "سوازيلاند",
            "السويد", "سويسرا", "الجمهورية العربية السورية", "تايوان",
            "طاجيكستان", "تنزانيا", "تايلاند", "توغو", "توكيلاو", "تونغا",
            "تونس", "تركيا", "تركمانستان", "توفالو",
            "الإمارات العربية المتحدة", "أوغندا", "المملكة المتحدة", "أوكرانيا",
            "أوروغواي", "أمريكا", "أوزبكستان", "فانواتو",
            "الكرسي الرسولي (دولة الفاتيكان)", "فنزويلا", "فيتنام",
            "واليس وفوتونا", "اليمن", "زامبيا", "زيمبابوي"};

    public static final String[] countryAreaCodes = { "93", "355", "213",
            "376", "244", "672", "54", "374", "297", "61", "43", "994", "973",
            "880", "375", "32", "501", "229", "975", "591", "387", "267", "55",
            "673", "359", "226", "95", "257", "855", "237", "1", "238", "236",
            "235", "56", "86", "61", "61", "57", "269", "242", "682", "506",
            "385", "53", "357", "420", "45", "253", "670", "593", "20", "503",
            "240", "291", "372", "251", "500", "298", "679", "358", "33",
            "689", "241", "220", "995", "49", "233", "350", "30", "299", "502",
            "224", "245", "592", "509", "504", "852", "36", "91", "62", "98",
            "964", "353", "44", "972", "39", "225", "1876", "81", "962", "7",
            "254", "686", "965", "996", "856", "371", "961", "266", "231",
            "218", "423", "370", "352", "853", "389", "261", "265", "60",
            "960", "223", "356", "692", "222", "230", "262", "52", "691",
            "373", "377", "976", "382", "212", "258", "264", "674", "977",
            "31", "687", "64", "505", "227", "234", "683", "850", "47", "968",
            "92", "680", "507", "675", "595", "51", "63", "870", "48", "351",
            "1", "974", "40", "7", "250", "590", "685", "378", "239", "966",
            "221", "381", "248", "232", "65", "421", "386", "677", "252", "27",
            "82", "34", "94", "290", "508", "249", "597", "268", "46", "41",
            "963", "886", "992", "255", "66", "228", "690", "676", "216", "90",
            "993", "688", "971", "256", "44", "380", "598", "1", "998", "678",
            "39", "58", "84", "681", "967", "260", "263" };

    private final LayoutInflater inflater;
    Context c;
    Typeface typeFace;
    TextView codeTv;
    RoundedImageView imgFlag;
    List<Country> mCountriesList;

    public CcPopUpItemAdapter(Context context, List<Country> mCountriesList, TextView code, RoundedImageView img){
        c = context;
        inflater = LayoutInflater.from(context);
        typeFace = Typeface.createFromAsset(c.getAssets(), "fonts/notosans_regular.ttf");
        this.codeTv = code;
        this.imgFlag = img;
        this.mCountriesList = mCountriesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.layout_cc_popup_item, parent, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Country country = mCountriesList.get(position);

        holder.name.setText(country.getName());
        holder.code.setText("+" + country.getCountryCode());
    }

    @Override
    public int getItemCount() {
        return mCountriesList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView code;
        TextView name;
        RelativeLayout container;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.ccName);
            code = (TextView) itemView.findViewById(R.id.ccCode);
            container = (RelativeLayout) itemView.findViewById(R.id.containerItem);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Country country = mCountriesList.get(getPosition());
            codeTv.setText("+" + country.getCountryCode());
            imgFlag.setImageResource(country.getResId());
            if (c instanceof PhoneNumberList) {
                PhoneNumberList.codeCountryTel = country.getCountryCode() + "";
            }
        }
    }
}
