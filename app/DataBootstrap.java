import java.util.Collections;

import javax.persistence.Query;

import org.apache.commons.lang.exception.ExceptionUtils;

import controllers.Application;
import models.Category;
import models.Emoticon;
import models.Icon;
import models.SystemInfo;
import models.Icon.IconType;
import models.Location;
import models.Location.LocationCode;
import models.SecurityRole;
import models.TermsAndConditions;
import models.User;
import play.db.jpa.JPA;
import providers.MyUsernamePasswordAuthUser;
import providers.MyUsernamePasswordAuthProvider.MySignup;

/**
 * data.DataBootstrap
 */
public class DataBootstrap {
    private static final play.api.Logger logger = play.api.Logger.apply(DataBootstrap.class);
    
    public static void bootstrap() {
        bootstrapTermsAndConditions();
        bootstrapIcon();
        bootstrapEmoticon();
        bootstrapSystemInfo();
        bootstrapUser();
        bootstrapLocation();
        bootstrapCategory();
	}
    
    private static void bootstrapTermsAndConditions() {
        Query q = JPA.em().createQuery("Select count(tnc) from TermsAndConditions tnc");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }
        
        logger.underlyingLogger().info("bootstrapTermsAndConditions()");

        TermsAndConditions tnc = new TermsAndConditions();
        tnc.terms =
                "<p>歡迎你加入babybox成為會員，babybox會員服務(以下稱會員服務)是由babybox Ltd(以下稱本公司)所建置提供，所有申請使用會員服務之使用者(以 下稱會員)，都應該詳細閱讀下列使用條款，這些使用條款訂立的目的，是為了保護會員服務的提供者以及所有使用者的利益，並構成使用者與會員服務提供者之間 的契約，使用者完成註冊手續、或開始使babybox所提供之會員服務時，即視為已知悉、並完全同意本使用條款的所有約定。</p>" +
                "<p>會員服務</p>" +
                "<p>一旦本公司完成並確認你的申請後，本公司將提供予你的免費會員服務內容有：會員專區、會員電子報，及其他未來可能新增之一般會員服務。會員服務之期間，是指使用者填妥申請表格並完成註冊程序後，本公司於完成相關系統設定、使會員服務達於可供使用之日。因會員服務所提供之所有相關網域名稱、 網路位址、功能以及其他一切因會員身分得享有之權益，均仍屬本公司或其他合法權利人所有，會員除僅得於服務期間內依本使用條款之約定為使用外，均不得以任 何方式將其轉讓、移轉、出租或出借予其他任何第三人。 會員服務僅依當時所提供之功能及狀態提供服務；本公司並保留新增、修改或取消會員服務內相關系統或功能之全部或一部之權利。帳號、密碼與安全性在使用會員 服務以前，必須經過完整的註冊手續，在註冊過程中你必須填入完整、而且正確的資料。在註冊過程中你可以自行選擇使用者名稱和密碼，但在使用會員服務的過程 中，你必須為經由這個使用者名稱和密碼所進行的所有行為負責。 對於你所取得的使用者名稱和密碼，你必須負妥善保管和保密的義務，如果你發現或懷疑這個使用者名稱和密碼被其他人冒用或不當使用，你必須立即通知 info&#64;baby-box.com.hk，讓本公司儘快採取適當之因應措施，但上述因應措施不得因此解釋為本公司明示或默示對你負有任何形式之賠償或補償之責任或義務。</p>" +
                "<p>使用者的行為</p>" +
                "<p>1. 任何未經事前授權的商業行為都是被禁止的。</p>" +
                "<p>2. 你必須遵守相關法律，並且對於經由使用者名稱和密碼所進行的任何行為、以及所儲存的所有資料負責。你必須同意不從事以下的行為：</p>" +
                "<p>(a) 傳送何任違反香港特別行政區法律之留言、討論、電郵，及私人訊息。</p>" +
                "<p>(b) 刊載、傳輸、發送或儲存任何誹謗、欺詐、傷害、猥褻、色情、賭博或其他一切違反法律之留言、討論、電郵、私人訊息、檔案或資料。</p>" +
                "<p>(c) 刊載、傳輸、發送或儲存任何侵害他人智慧財產權或其他權益的資料。</p>" +
                "<p>(d) 未經同意收集他人電子郵件位址以及其他個人資料。</p>" +
                "<p>(e) 未經同意擅自摘錄或使用會員服務內任何資料庫內容之部份或全部。</p>" +
                "<p>(f) 刊載、傳輸、發送、儲存病毒、或其他任何足以破壞或干擾電腦系統或資料的程式或訊息。</p>" +
                "<p>(g) 破壞或干擾會員服務的系統運作或違反一般網路禮節之行為。</p>" +
                "<p>(h) 在未經授權下進入會員服務系統或是與系統有關之網路、或冒用他人帳號或偽造寄件人辨識資料傳送郵件或留言，企圖誤導收件人之判斷。</p>" +
                "<p>(i) 任何妨礙或干擾其他使用者使用會員服務之行為。</p>" +
                "<p>(j)傳送幸運連鎖信、垃圾郵件、廣告信或其他漫無目的之訊息。</p>" +
                "<p>(k)於站內進行任可未經許可的銷售及訂購活動。</p>" +
                "<p>(l)於未經許可的情況下在站內進行與保險、投資、傳銷、市場調查、座談會、僱傭及各類轉介等有關之宣傳及行銷活動。</p>" +
                "<p>(m)任何透過不正當管道竊取會員服務之會員帳號、密碼或存取權限之行為。</p>" +
                "<p>(n)其他不符合會員服務所提供的使用目的之行為。</p>" +
                "<p>責任限制</p>" +
                "<p>本公司所提供之各項會員服務，均僅依各該服務當時之功能及現況提供使用， 對於使用者之特定要求或需求，包括但不限於速度、安全性、可靠性、完整性、正確性及不會斷線和出錯等，本公司不負任何形式或內容之擔保或保證責任。 本公司不保證任何郵件、檔案或資料於傳送過程均可靠且正確無誤，亦不保證所儲存或所傳送之郵件、檔案或資料之安全性、可靠性、完整性、正確性及不會斷線和 出錯等，會員應對傳送過程中或儲存時之郵件、檔案或資料遺失或電腦系統損壞自負完全責任，與本公司無涉。因本公司所提供的會員服務本身之使用，所造成之任 何直接或間接之損害，本公司均不負任何責任，即使係本公司曾明白建議之注意事項亦同。</p>" +
                "<p>服務暫停或中斷</p>" +
                "<p>1.在下列情形，本公司將暫停或中斷本服務之全部或一部，且對使用者任何直接或間接之損害，均不負任何責任：</p>" +
                "<p>(a)對本服務相關軟硬體設備進行搬遷、更換、升級、保養或維修時。</p>" +
                "<p>(b)使用者有任何違反政府法律或本使用條款情形。</p>" +
                "<p>(c)天災或其他不可抗力所致之服務停止或中斷。</p>" +
                "<p>(d)其他不可歸責於本公司之事由所致之服務停止或中斷。</p>" +
                "<p>2.會員服務系統或功能『例行性』之維護、改置或變動所發生之服務暫停或中斷，本公司將於該暫停或中斷前以電子郵件、公告或其他適當之方式告知會員。</p>" +
                "<p>3.因使用者違反相關法令或本使用條款、或依相關主管機關之要求、或因其他不可歸 責於本公司之事由，而致本服務全部或一部暫停或中斷時，暫停或中斷期間仍照常計費。</p>" +
                "<p>終止服務</p>" +
                "<p>1.基於公司的運作，會員服務有可能停止提供服務之全部或一部，使用者不可以因此而要求任何賠償或補償。</p>" +
                "<p>2.如果你違反了本使用條款，本公司保留隨時暫時停止提供服務、或終止提供服務之權利，你不可以因此而要求任何賠償或補償。</p>" +
                "<p>3.如果你在會員服務上所刊載、傳輸、發送或儲存的郵件、檔案或資料，有任何違反法令、違反本使用條款、或有侵害第三人權益之虞者，本公司保留隨時得不經通知直接加以移動或刪除之權利。若本公司因此受到任何損害，你應對本公司負損害賠償之責任。</p>" +
                "<p>4.會員服務系統會自動偵測沒有使用的帳號，如超過九十天均未使用， 本公司將有權將會員的帳戶、檔案或資料全數刪除且不予另行備份而毋需另行通知，如你需重新啟用該電子郵件帳號，請另與網站管理者聯繫。上述有無使用之紀錄，均以會員服務系統內留存之紀錄為準。</p>" +
                "<p>修改本使用條款的權利</p>" +
                "<p>本公司保留隨時修改本會員服務使用規則的權利，修改後的會員服務使用條款將公佈在本公司的網站上，不另外個別通知使用者。</p>" +
                "<p>準據法及管轄權</p>" +
                "<p>1.本約定條款解釋、補充及適用均以香港特別行政區為準據法。</p>" +
                "<p>2.因本約定條款所發生之訴訟，以香港特別行政區法院為第一審管轄法院。</p>";
        tnc.privacy =
                "<p>babybox網站為閣下提供最佳服務及尊重閣下的個人私隱。因此，本公司在搜集、維護及使用個人資料時，保證遵守香港法例第 486 章《個人資料（私隱）條例》的要求。</p>" + 
                "<p>要求資料</p>" + 
                "<p>babybox網站有數處地方可能要求閣下提供個人資料，這些資料可能包括：</p>" + 
                "<p>- 登入名稱及密碼</p>" + 
                "<p>- 姓名</p>" + 
                "<p>- 住址地區</p>" + 
                "<p>- 性別</p>" + 
                "<p>- 出生年份 </p>" + 
                "<p>- 電郵地址</p>" + 
                "<p>- 小孩子性別</p>" + 
                "<p>- 小孩子出生日期</p>" + 
                "<p>當登記成為會員或使用babybox網站時，本公司可能要求閣下提供以上資料。閣下同意所提供給本公司的資料為正確、真實、有效和完整。</p>" + 
                "<p>資料使用</p>" + 
                "<p>若資料為不正確，不真實，無效或不完整，本公司有權利取消閣下用戶之註冊或使用網站之權利及服務。閣下知道及同意，你對所提供之資料的內容和準確性須負上所有責任。</p>" + 
                "<p>本公司偶爾會使用閣下所提供的資料通知閣下babybox網站的轉變、新設的服務，及與閣下息息相關的優惠。如不想取得這些資料，可於電郵 info&#64;baby-box.com.hk 通知我們。 閣下知道、同意及授權所提供的資料可能供給、披露及供存取予以下人士或公司及作出以下用途：</p>" + 
                "<p>- 本公司及 / 或本公司集團內的任何人士或公司；</p>" + 
                "<p>- 須向本公司履行保密責任之任何人士或公司；</p>" + 
                "<p>- 為上述目的或與上述目的有關而聘用之任何合約承包商、代理商、公司，或向本公司提供行政、電訊、電腦、付賬、專業服務或其他服務的公司；</p>" + 
                "<p>- 分析、核實及檢查閣下的信用、付款及相關服務的狀況；</p>" + 
                "<p>- 處理閣下要求的任何付款操作；</p>" + 
                "<p>- 收取閣下賬戶與服務有關的未付金額；</p>" + 
                "<p>- 與用戶有交往或計劃有交往之任何銀行或金融機構；或</p>" + 
                "<p>- 任何獲本公司轉讓或計劃轉讓權益及/或責任之人士或公司，而此等權益及 / 或責任與用戶或與向閣下提供之產品或服務有關。</p>" + 
                "<p>- 為向閣下提供購買之產品及服務，本公司需要向服務供應商或第三中介人提供用戶個人資料。如未能提供資料，本公司將不能有效地提供有關服務予閣下。</p>" + 
                "<p>- 部份資料可能會透過'cookies'收集，閣下可自行更改瀏覽的設定而使cookies失效。</p>" + 
                "<p>統計性資料</p>" + 
                "<p>請注意本公司有可能向第三者提供本公司客戶的統計資料，但這些統計資料將不會提及任何個別客戶。</p>" + 
                "<p>保護資料</p>" + 
                "<p>為防止不授權登入、保持數據的安全性及確保資料得到正確的運用，本公司不論在實體、電子化及管理上皆制定了合適程序去保障及保護本公司所收集的資料的安全性。</p>" + 
                "<p>連接到其他網站</p>" + 
                "<p>閣下可從babybox網站連接到其他網站，但請注意該等網站的私隱政策很可能與本公司的不同。本公司建議閣下在該等網站披露其個人資料前，先詳細閱讀其私隱政策。[在任何情況下，該等網站都不會與本公司分享用戶的個人資料。]</p>" + 
                "<p>聊天室、交換電郵、告示板及網誌</p>" + 
                "<p>請留意若閣下自願在聊天室、交換電郵、告示板及網誌上公開披露其個人資料，該等資料很可能被收集及被他人使用及可能導致閣下因公開資料而收到其他不必要的訊息，本公司毋須就以上情況負責。</p>" + 
                "<p>登入/修改/更新個人資料</p>" + 
                "<p>閣下可以隨時在babybox裏修改及更新你的個人資料。會員戶口是受到密碼保護的，因此只有該會員能登入及檢視其會員戶口資料。</p>" +
                "<p>通訊協定地址</p>" + 
                "<p>為方便管理伺服器及系統，本公司會收集用戶的通訊協定地址。請注意在babybox網站上的連接網站很可能會收集閣下的個人資料，本私隱政策並不包括該等網站對其資料的處理及政策。</p>" + 
                "<p>私隱政策改動</p>" + 
                "<p>若本私穩政策有任何改動，本公司會在這裏刊登更新的政策及有關條文，以便閣下能查閱有關政策。閣下繼續使用babybox網站代表你接受所有已更改的條款。除非有更新聲明，本公司不會在閣下沒有機會拒絕或避免的情況下，把你的個人資料運用在新的用途上。</p>" + 
                "<p>立法解除條款</p>" + 
                "<p>在法律要求下，本公司或會披露閣下個人資料而該等行為是必需的。本公司確信資料公開能保障及維護本公司、用戶及他人的權利、財產及安全，並相信該等資料會依據正確法律程序處理。</p>" + 
                "<p>聯絡我們</p>" +
                "<p>若用戶有任何有關安全與私隱的問題，請電郵至 info&#64;baby-box.com.hk 與我們聯絡。</p>";
        tnc.save();
    }

    private static void bootstrapEmoticon() {
        Query q = JPA.em().createQuery("Select count(i) from Emoticon i");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }

        logger.underlyingLogger().info("bootstrapEmoticon()");
        
        Emoticon emoticon = null; 
        emoticon = new Emoticon("angel", "O:)", 6, "/assets/app/images/emoticons/angel.png");
        emoticon.save();
        emoticon = new Emoticon("bad", "X-(", 12, "/assets/app/images/emoticons/bad.png");
        emoticon.save();
        emoticon = new Emoticon("blush", "^_^", 4, "/assets/app/images/emoticons/blush.png");
        emoticon.save();
        emoticon = new Emoticon("cool", "B)", 9, "/assets/app/images/emoticons/cool.png");
        emoticon.save();
        emoticon = new Emoticon("cry", ":'(", 11, "/assets/app/images/emoticons/cry.png");
        emoticon.save();
        emoticon = new Emoticon("dry", ":_", 15, "/assets/app/images/emoticons/dry.png");
        emoticon.save();
        emoticon = new Emoticon("frown", ":(", 10, "/assets/app/images/emoticons/frown.png");
        emoticon.save();
        emoticon = new Emoticon("gasp", ":O", 19, "/assets/app/images/emoticons/gasp.png");
        emoticon.save();
        emoticon = new Emoticon("grin", ":D", 3, "/assets/app/images/emoticons/grin.png");
        emoticon.save();
        //emoticon = new Emoticon("happy", "^^D", "/assets/app/images/emoticons/happy.png");
        //emoticon.save();
        emoticon = new Emoticon("huh", "O_o", 17, "/assets/app/images/emoticons/huh.png");
        emoticon.save();
        emoticon = new Emoticon("laugh", "XD", 16, "/assets/app/images/emoticons/laugh.png");
        emoticon.save();
        emoticon = new Emoticon("love", "**)", 5, "/assets/app/images/emoticons/love.png");
        emoticon.save();
        emoticon = new Emoticon("mad", "X(", 13, "/assets/app/images/emoticons/mad.png");
        emoticon.save();
        emoticon = new Emoticon("ohmy", ";O", 14, "/assets/app/images/emoticons/ohmy.png");
        emoticon.save();
        emoticon = new Emoticon("ok", ":|", 20, "/assets/app/images/emoticons/ok.png");
        emoticon.save();
        emoticon = new Emoticon("smile", ":)", 1, "/assets/app/images/emoticons/smile.png");
        emoticon.save();
        emoticon = new Emoticon("teat", ":+O", 21, "/assets/app/images/emoticons/teat.png");
        emoticon.save();
        emoticon = new Emoticon("teeth", "^^]", 8, "/assets/app/images/emoticons/teeth.png");
        emoticon.save();
        emoticon = new Emoticon("tongue", ":p", 7, "/assets/app/images/emoticons/tongue.png");
        emoticon.save();
        emoticon = new Emoticon("wacko", ":S", 18, "/assets/app/images/emoticons/wacko.png");
        emoticon.save();
        emoticon = new Emoticon("wink", ";)", 2, "/assets/app/images/emoticons/wink.png");
        emoticon.save();
	}

    private static void bootstrapIcon() {
        Query q = JPA.em().createQuery("Select count(i) from Icon i");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }
        
        logger.underlyingLogger().info("bootstrapIcon()");
        
        Icon icon = null;
        
        // Category icons
        icon = new Icon("feedback", IconType.CATEGORY, "/assets/app/images/general/icons/category/feedback.png");
        icon.save();
        icon = new Icon("cat", IconType.CATEGORY, "/assets/app/images/general/icons/category/cat.png");
        icon.save();
        icon = new Icon("helmet", IconType.CATEGORY, "/assets/app/images/general/icons/category/helmet.png");
        icon.save();
        icon = new Icon("book", IconType.CATEGORY, "/assets/app/images/general/icons/category/book.png");
        icon.save();
        icon = new Icon("gift_box", IconType.CATEGORY, "/assets/app/images/general/icons/category/gift_box.png");
        icon.save();
        icon = new Icon("balloons", IconType.CATEGORY, "/assets/app/images/general/icons/category/balloons.png");
        icon.save();
        icon = new Icon("camera", IconType.CATEGORY, "/assets/app/images/general/icons/category/camera.png");
        icon.save();
        icon = new Icon("music_note", IconType.CATEGORY, "/assets/app/images/general/icons/category/music_note.png");
        icon.save();
        icon = new Icon("plane", IconType.CATEGORY, "/assets/app/images/general/icons/category/plane.png");
        icon.save();
        icon = new Icon("shopping_bag", IconType.CATEGORY, "/assets/app/images/general/icons/category/shopping_bag.png");
        icon.save();
        icon = new Icon("spoon_fork", IconType.CATEGORY, "/assets/app/images/general/icons/category/spoon_fork.png");
        icon.save();
        icon = new Icon("ball", IconType.CATEGORY, "/assets/app/images/general/icons/category/ball.png");
        icon.save();
        icon = new Icon("boy", IconType.CATEGORY, "/assets/app/images/general/icons/category/boy.png");
        icon.save();
        icon = new Icon("girl", IconType.CATEGORY, "/assets/app/images/general/icons/category/girl.png");
        icon.save();
        icon = new Icon("bottle", IconType.CATEGORY, "/assets/app/images/general/icons/category/bottle.png");
        icon.save();
        icon = new Icon("bed", IconType.CATEGORY, "/assets/app/images/general/icons/category/bed.png");
        icon.save();
        icon = new Icon("stroller", IconType.CATEGORY, "/assets/app/images/general/icons/category/stroller.png");
        icon.save();
        icon = new Icon("teddy", IconType.CATEGORY, "/assets/app/images/general/icons/category/teddy.png");
        icon.save();
        icon = new Icon("icecream", IconType.CATEGORY, "/assets/app/images/general/icons/category/icecream.png");
        icon.save();
        icon = new Icon("sun", IconType.CATEGORY, "/assets/app/images/general/icons/category/sun.png");
        icon.save();
        icon = new Icon("rainbow", IconType.CATEGORY, "/assets/app/images/general/icons/category/rainbow.png");
        icon.save();
        icon = new Icon("cloud", IconType.CATEGORY, "/assets/app/images/general/icons/category/cloud.png");
        icon.save();
        icon = new Icon("loc_area", IconType.CATEGORY, "/assets/app/images/general/icons/category/loc_city.png");
        icon.save();
        icon = new Icon("loc_area", IconType.CATEGORY, "/assets/app/images/general/icons/category/loc_area.png");
        icon.save();
        icon = new Icon("loc_district", IconType.CATEGORY, "/assets/app/images/general/icons/category/loc_district.png");
        icon.save();
    }
    
    private static void bootstrapSystemInfo() {
        SystemInfo systemInfo = SystemInfo.getInfo();
        if (systemInfo == null) {
            systemInfo = new SystemInfo();
            systemInfo.save();
        }
    }
    
    private static void bootstrapUser() {
        Query q = JPA.em().createQuery("Select count(u) from User u where system = true and deleted = 0");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }
        
        logger.underlyingLogger().info("bootstrapUser()");
        
        // signup info for super admin
        MySignup signup = new MySignup();
        signup.email = "mybabybox.app@gmail.com";
        signup.fname = "babybox";
        signup.lname = "HK";
        signup.password = "myBabyB0x";
        signup.repeatPassword = "myBabyB0x";
        
        MyUsernamePasswordAuthUser authUser = new MyUsernamePasswordAuthUser(signup);
        User superAdmin = User.create(authUser);
        
        superAdmin.roles = Collections.singletonList(
                SecurityRole.findByRoleName(SecurityRole.RoleType.SUPER_ADMIN.name()));
        superAdmin.name = User.BB_ADMIN_NAME;
        superAdmin.displayName = User.BB_ADMIN_NAME;
        superAdmin.emailValidated = true;
        superAdmin.newUser = false;
        superAdmin.system = true;
        superAdmin.save();
        
        /*
        try {
            superAdmin.setPhotoProfile(new File(Resource.STORAGE_PATH + "/default/logo/logo-mB-1.png"));
        } catch (IOException e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        }
        */
    }
    
    private static void bootstrapLocation() {
        Query q = JPA.em().createQuery("Select count(l) from Location l");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }
        
        logger.underlyingLogger().info("bootstrapLocation()");
        
        Location countryHK = new Location(LocationCode.HK, "香港", "全香港");    // country
        JPA.em().persist(countryHK);
        Location stateHK = new Location(countryHK, "香港", "全香港");            // state
        JPA.em().persist(stateHK);
        Location cityHK = new Location(stateHK, "香港", "全香港");               // city
        JPA.em().persist(cityHK);
        
        Location hkIsland = new Location(cityHK, "香港島");    // region
        JPA.em().persist(hkIsland);
        Location d1 = new Location(hkIsland, "中西區");        // district
        JPA.em().persist(d1);
        Location d2 = new Location(hkIsland, "東區");
        JPA.em().persist(d2);
        Location d3 = new Location(hkIsland, "南區");
        JPA.em().persist(d3);
        Location d4 = new Location(hkIsland, "灣仔區");
        JPA.em().persist(d4);
        
        Location kowloon = new Location(cityHK, "九龍");      // region
        JPA.em().persist(kowloon);
        Location d5 = new Location(kowloon, "九龍城區");       // district
        JPA.em().persist(d5);
        Location d6 = new Location(kowloon, "觀塘區");
        JPA.em().persist(d6);
        Location d8 = new Location(kowloon, "深水埗區");
        JPA.em().persist(d8);
        Location d9 = new Location(kowloon, "黃大仙區");
        JPA.em().persist(d9);
        Location d10 = new Location(kowloon, "油尖旺區");
        JPA.em().persist(d10);
        
        Location newTerritories = new Location(cityHK, "新界");   // region
        JPA.em().persist(newTerritories);
        Location d7 = new Location(newTerritories, "西貢區");  // district
        JPA.em().persist(d7);
        Location d11 = new Location(newTerritories, "北區");
        JPA.em().persist(d11);
        Location d12 = new Location(newTerritories, "沙田區");
        JPA.em().persist(d12);
        Location d13 = new Location(newTerritories, "大埔區");
        JPA.em().persist(d13);
        Location d14 = new Location(newTerritories, "葵青區");
        JPA.em().persist(d14);
        Location d15 = new Location(newTerritories, "荃灣區");
        JPA.em().persist(d15);
        Location d16 = new Location(newTerritories, "屯門區");
        JPA.em().persist(d16);
        Location d17 = new Location(newTerritories, "元朗區");
        JPA.em().persist(d17);
        
        Location islands = new Location(cityHK, "離島");      // region
        JPA.em().persist(islands);
        Location d18 = new Location(islands, "離島區");        // district
        JPA.em().persist(d18);
    }
    
    private static void bootstrapCategory() {
        Query q = JPA.em().createQuery("Select count(c) from Category c where system = true");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }
        
        logger.underlyingLogger().info("bootstrapCategory()");
        
        String name = "童裝童鞋";
        String desc = "童裝童鞋";
        createCategory(name, desc, "/assets/app/images/category/cat_clothes.jpg", 1);
        
        name = "玩具教材";
        desc = "玩具教材";
        createCategory(name, desc, "/assets/app/images/category/cat_toys.jpg", 2);
               
        name = "BB用品";
        desc = "BB用品";
        createCategory(name, desc, "/assets/app/images/category/cat_utils.jpg", 3);
        
        name = "生活家居";
        desc = "生活家居";
        createCategory(name, desc, "/assets/app/images/category/cat_home.jpg", 4);
        
        name = "奶粉尿片";
        desc = "奶粉尿片";
        createCategory(name, desc, "/assets/app/images/category/cat_diaper.jpg", 5);
        
        name = "其它";
        desc = "其它";
        createCategory(name, desc, "/assets/app/images/category/cat_other.jpg", 6);
    }

    private static Category createCategory(String name, String desc, String icon, int seq) {
    	Category category = null;
        try {
        	category = Application.getBBAdmin().createCategory(name, desc, icon, seq);
            //category.setCoverPhoto(new File(Resource.STORAGE_PATH + "/default/box_cover.jpg"));
        } catch (Exception e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        }
        return category;
    }
}