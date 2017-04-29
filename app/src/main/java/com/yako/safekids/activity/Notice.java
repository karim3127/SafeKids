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

public class Notice extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txtDesc)
    TextView txtDesc;
    private SharedPreferences prefs;
    private String langage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");

        if (langage.equals("ar")) {
            setContentView(R.layout.activity_notice);

        } else {
            setContentView(R.layout.activity_notice_en);
        }

        ButterKnife.bind(this);
        if(langage.equals("ar")){
            txtDesc.setText("التعليمات وشروط الاستخدام لتطبيق Safe Kids\n" +
                    "\n" +
                    "1 - تسجيل البيانات الصحيحه \n" +
                    "الاسم الكامل  ، البلدة ، رقم الهاتف ، رقم السياره نوعها ولونها لكي يتم عمل التطبيق  بشكل صحيح لحماية الطفل\n" +
                    "\n" +
                    "2 يجب كتابه ارقام الطوارى بشكل صحيح حتى يتم التواصل معهم في حال فقدان الاتصال مع الام ليتم انقاذ الطفل .\n" +
                    "\n" +
                    "3 - عدم ضبط واستعمال التطبيق اثناء القياده .\n" +
                    "\n" +
                    "4 - نحن غير مسؤولين عن اي ضرر يحدث لكم في حال توقف عمل التطبيق أو في حالة حدوث اَي خلل تقني \n" +
                    "\n" +
                    "5 -  نحن غير مسؤولين في حال ضبط التطبيق بشكل غير صحيح .\n" +
                    "\n" +
                    "6 - لا نستخدم بيناتكم لأمور تسويقية فقط من اجل تطوير التطبيق . نحن نريد فقط الحفاظ على امن وسلامة اطفالكم وجميع بيناتكم الخاصه مشفرة .\n" +
                    "\n" +
                    "7 - نستخدم معلومات ال Gbs الخاصه بكم فقط لتحديد عنوان الطفل المفقود وتحديد السرعه وليس لدينا اي استعمال او نوايا  اخرى .\n" +
                    "\n" +
                    "8 - استعمال  التطبيق لا يمنع مثل هذه الحوادث بل يقلل من حدوثها لذلك يجب عليكم اتخاذ جميع احتياطاتكم .\n" +
                    "\n" +
                    "9 التطبيق مخصص من اجل مساعدة الأهل فقط ، لذلك لا تقع اَي مسوليه على مبرمج التطبيق وتقع المسؤليه عليكم فقط واستعمالك لتطبيق هو يعني موافقتك لشروط الاستخدام والتعليمات ..\n" +
                    "\n" +
                    "وشكرا");
        }else if (langage.equals("he")){
            txtDesc.setText("מידע כללי והוראות הפעלה לאפלקציית Safe Kids\n" +
                    "1- רישום מידע מדויק\n" +
                    "שם מלא, עיר, מס' טלפון, מס' רכב, סוג וצבע הרכב על מנת לוודא פעילות תקינה של האפלקצייה לשמירה על שלום הילדים.\n" +
                    "\n" +
                    "2- צריך לוודא שמספרי החירום הרשומים נכונים על מנת לאפשר לאפלקצייה ליצור קשר מוקדי החירום להצלת הילדים.\n" +
                    "\n" +
                    "3- ממולץ לא להשתמש באפלקצייה בזמן הנהיגה.\n" +
                    "\n" +
                    "4- איננו נושאים באף אחריות לכל נזק אשר ייגרם במידה והאפלקצייה תפסיק לעבוד או בכל תקלה טכנית.\n" +
                    "\n" +
                    "5- איננו נושאים באף אחריות במידה והותקנה האפלקצייה באופן שגוי.\n" +
                    "\n" +
                    "6- כל המידע שאנו אוספים מיועד לשיפור המערכת ואיננו מיועד לצורכי שיווק. המטרה שלנו היא לשמור על חיי ילדיכם\n" +
                    "\n" +
                    "7- מידע ה- gps מיועד לקביעת מיקום הילדים איננו משמש לאף מטרה אחרת.\n" +
                    "\n" +
                    "8- השימוש באפלקצייה אינו מונע מקרים של שכחת ילדים, אך הוא מקטין את הסיכוי להתרחשותם, ולכן אתם נדרשים לנקוט בכל אמצעי הזהירות.\n" +
                    "\n" +
                    "9- האפלקצייה נועדה לעזור להורים, ועל כן מפתחי האפלקצייה לא נושאים באף אחריות וכל האחריות היא של המשתמשים בלבד. בהפעלתכם את האפלקצייה הינכם מצהירים על הסכמתכם על תנאי השימוש בה ..");
        }else{
            txtDesc.setText("Terms and conditions Safe Kids application.\n" +
                    "\n" +
                    "1 Register with correct information of full name, city, mobile number, car's number, car's model and colour so the application works correctly to save your child.\n" +
                    "\n" +
                    "2 Fill the list of emergency numbers correctly in order to contact them in case of losing contact with the mother to save the kid's life.\n" +
                    "\n" +
                    "3. Avoid adjusting or using the application while driving.\n" +
                    "\n" +
                    "4. We are not responsible for any harm that may happen to you in case the application stops working or a technical problem occurs.\n" +
                    "\n" +
                    "5. We are not responsible if you adjust the application in a wrong way.\n" +
                    "\n" +
                    "6. We do not use your  information for any marketing targets, we use them to improve the application. We only aim to keep you children safe and your information encrypted.\n" +
                    "\n" +
                    "7. We only use the gps data to detect the location for the lost child and the speed of your car. We don't have any further intentions to use it for any other case.\n" +
                    "\n" +
                    "8. Using the application will not prevent accidents but it reduces them so you have to take all the precautions into consideration.\n" +
                    "\n" +
                    "9. The application is made to help only the parents, so there is no responsibility on application programmer and it's only your responsibility. Using the application means your agreement to the application conditions and instructions.");
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
