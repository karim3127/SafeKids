package com.yako.safekids.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.safekids.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class About extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txtDesc)
    TextView txtDesc;
    private SharedPreferences prefs;
    String langage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");

        if (langage.equals("ar")) {
            setContentView(R.layout.activity_about);
        } else {
            setContentView(R.layout.activity_about_en);
        }

        ButterKnife.bind(this);

        if(langage.equals("ar")){
            txtDesc.setText("تطبيق Safe Kids اول تطبيق عالمي مجاني بهدف انهاء ظاهرة نسيان الأطفال داخل المركبة ..\n" +
                    "\n" +
                    " التطبيق مخصص لحمايه الأطفال من المخاطر على مدار الساعة لمنع نسيان اطفالنا داخل المركبة بامكانيات مميزه وذكيه ويحافظ على سلامة وامن اطفالكم بنسبه 99% في حال استخدام  التطبيق بشكل صحيح\n" +
                    "التطبيق  من برمجه وتطوير المهندس احمد بطو ويتميز بالخصائص التاليه\n" +
                    "\n" +
                    "1 - نظام ذكي لحمايه أطفالك على مدار 24 ساعه ولا يحتاج اي جهاز اضافي داخل السياره\n" +
                    " \n" +
                    "2 - تطبيق safe kids الذكي  يعلم  بشكل تلقائي  في حال توقف المركبة الخاصة بكم ويرسل تنبيه \" أنا طفلكم معكم \"\n" +
                    " و يمكن ايضا اختيار إمكانية تحديد وقت للعودة الى المركبة بعد دقائق ويقوم في تنبيهكم بعد انتهاء الوقت .\n" +
                    "\n" +
                    "3 - في حال نسيان الطفل داخل المركبة يقوم التطبيق بالاتصال بشكل تلقائي لهاتف الان لكي تعود الى المركبة وان لم يتلقى رد من الام ، \n" +
                    "في هذه الحاله يقوم التطبيق بارسال رساله بخريطة المكان التي توقفت فيه المركبة ونوعها رقمها ولونها الى \" ارقام الطوارئ \" المسجله في التطبيق ومعلومات اضافية عن درجه الحرارة والرطوبة في ذلك الوقت ليتم انقاذ الطفل من خطر محتم .\n" +
                    "\n" +
                    "4 - تطبيق سيف كيديز يتابع تحركاتكم اثناء تنقلكم في المركبة ويرسل انذار اذا كان هناك عائق يشكل خطر على حياتكم وحياة اطفالكم \n" +
                    " مثل : تشويشات الشارع و حيوانات في الطريق قبل كم من مكان تواجدهم\n" +
                    "\n" +
                    "5 - تطبيق سيف كيديز الذكي  يتابع سرعة المركبة الخاصه بكم في حال تجاوزتم السرعه القانونيه يقوم بإرسال تنبيه لتخفيض السرعه والحفاظ على امنكم وحياة اطفالكم .\n" +
                    "\n" +
                    " 6- تطبيق سيف كيديز يتمتع بخاصية(Safe Kids Near) \n" +
                    "في حال نسيان الطفل داخل المركبة ولا يوجد رد من الأهل وايضاً من ارقام الطوارئ يقوم بالارسال لأقرب شخص في المنطقة لإنقاذ حياه الطفل .\n" +
                    "\n" +
                    "7 -في كل حساب في التطبيق  نظام لضبط المواعيد لكل طفل داخل ملفه الشخصي وتسجيل مكان تواجدهم اخر مره كي لا تنساهم داخل المركبة .");
        }else if (langage.equals("he")){
            txtDesc.setText("יישום Safe Kids היא האפלקצייה הראשונה בעולם  ללא תשלום שמטרתה לשים קץ לתופעת שכיחת הילדים  בתוך הרכב\n" +
                    "\n" +
                    "האפלקצייה מיועדת להגנה על הילדים מסכנות למשך 24 שעות למניעת שכיחת הילדים ברכב עם יתרונות ייחודיים ושומר על בטיחות הילדים שלכם  ב 99% אם השתמשתם באפלקצייה בדרך נכונה\n" +
                    "\n" +
                    "  1- מערכה חכמה כדי להגן על ילדיך  למשך 24 שעות  ולא צריך להשתמש באף מכשיר אחר ברכב\n" +
                    "\n" +
                    "2- אפלקציית Safe Kids החכמה עובדת (או יודעת לזהות) באופן אוטומטי את עצירת הרכב, ושולחת התראה \"אני הילד שלכם איתכם\"\n" +
                    "בנוסף קיימת אשפרות לבחור את מועד חזרתכם לרכב, והאפלקצייה תדאג ליידע אותכם במקרה ואתם לא חוזרים בזמן.\n" +
                    "\n" +
                    "3- במקרה שכיחת הילד בתוך הרכב האפלקציה מתקשרת באופן אוטומטי לאמא כדי שתחזור לרכב ואם האפלקצייה לא מקבלת תשובה מאמא ,  במקרה הזה האפלקציה שולחת הודעה במפת המיקום שבוא נמצא הרכב ודגם שלה , מספר וצבע הרכב ל מספרי חירום שנרשמו בהאפלקציה\n" +
                    "\n" +
                    "4- אפליקציה  Safe Kids עוקב התנועות שלכם בזמן הנהיגה ושולח התראה אם היה מכשול בדרך מהווה סכנת חיים לך ולילדים שלך .\n" +
                    "\n" +
                    "5 - יישום Safe Kids החכם מעקב מהירות הרכב בזמן נהיגה ואם עברתם המהירות החוקית האפלקצייה שולחת התראה שתצריך להאט המהירות ולשמור על הבטיחות שלכם .\n" +
                    "\n" +
                    "6- ( Safe Kids near) \n" +
                    "במקרה ושכחתם הילד בתוך הרכב והיישום לא קיבל תשובה מהורים וגם ממספרי חירום ، הוא שולח לבן אדם הכי קרוב למיקום הרכב להציל את חייו לילד\n" +
                    "\n" +
                    "7- בכל חשבון באפלקצייה יש מערכת לקביעת זמן לכל ילד בתוך החשבון שלו ובאיזה מקום היה בזמן האחרן כי לא לשכוח אותם בתוך הרכב");
        }else{
            txtDesc.setText("Safe Kids is the first international free application to end the phenomenon of forgetting kids inside the vehicles. \n" +
                    "The application is designed to protect the children from danger all the time to prevent forgetting the kids inside the vehicles with special and smart capabilities so it saves your children of 99% in case of correct usage of the application. The application was designed and developed by the engineer Ahmad Butto. This application has special features:\n" +
                    "\n" +
                    "1. A smart system to protect your children 24 hours and it doesn't need any other device inside your car.\n" +
                    "\n" +
                    "2. Safe Kids application automatically senses once you pull over your car and it starts to send alerts\" I am your child and I am with you inside this car\"\n" +
                    "It is also possible to choose the option of determining the time of coming back to the car within couple of minutes and so the application will alert you when the time is over.\n" +
                    "\n" +
                    "3. In case of forgetting the child inside the car, the application automatically calls the mother (the driver) so she gets back to the car. If the mother does not reply, the application sends a text message to the emergency numbers that are saved in the application, the text message includes the location of the car, it's color, it's number , the humidity and temperature  at that time so the child can be saved from an inevitable danger.\n" +
                    "\n" +
                    "4. Safe kids application follows your movements while driving the car and it also alerts you in case there is an obstacle or danger that threatens your life of your children lives. Such as: streets' disturbance, animals crossing the road you will be alerted of those threatens which are some kilometers away of your current location.\n" +
                    "\n" +
                    "5 Safe kids application tracks the speed of your car and in case of exceeding the speed limit it will warn you to decrease the speed and keep you and your kids safe.");
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        if (langage.equals("ar") || langage.equals("he")) {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_normal_white, menu);
        if (langage.equals("en")) {
            menu.findItem(R.id.action_back).setVisible(false);
            menu.findItem(R.id.action_back).setEnabled(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (langage.equals("en")) {
                onBackPressed();
                return true;
            }
        }

        if (id == R.id.action_back) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
