/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Test_Dates;

import com.filenet.api.collection.DocumentSet;
import com.filenet.api.collection.FolderSet;
import com.filenet.api.collection.StringList;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;
import external_functions.Domino;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import javax.security.auth.Subject;
import lotus.domino.NotesException;

/**
 *
 * @author nurzhan.trimov
 */
//Уведомление по плану мероприятий.
public class Select_2days_Before {

    public static StringList EmailList;
    public static String Subject = "Уведомление";
    public static String Body = "Окончание срока через 2 день";
    private static String dominoServer = "10.8.1.102:63148";
    private static String dominoMailbox = "mail\\tkab501-3.nsf";
    private static String dominoUsername = "test3 kab501-3";
    private static String dominoPassword = "test3";

    public static void main(String args[]) {

        Select_1day_Before p8 = new Select_1day_Before();
        ObjectStore store = p8.getP8Connection();
        Domino sendmail = new Domino();

        Date date = new Date();
        final String ISO_FORMAT = "yyyyMMdd'T'HHmmss'Z'";

        final SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
        final TimeZone utc = TimeZone.getTimeZone("UTC");
        sdf.setTimeZone(utc);
        System.out.println(sdf.format(date));



        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, +1);
        final Date PastDate = c.getTime();
        System.out.println("PastDate= " + PastDate);

        c.setTime(date);
        c.add(Calendar.DATE, +3);
        final Date FutureDate = c.getTime();
        System.out.println("FutureDate= " + FutureDate);
        System.out.println("===========");
        System.out.println("");
        System.out.println("");


        //NOTIFICATION DATE FORMAT
        String format = "dd.MM.yyyy";
        SimpleDateFormat message_format = new SimpleDateFormat(format);


//String query = "select * from FinInspect where DateRefItogFO>"+"20120920T080000Z";
        //String query = "select * from FinInspect where DateSubmPlan>" + sdf.format(date);
        String query = "select * from FinInspect where DateSubmPlan> " + sdf.format(PastDate) + " and DateSubmPlan<" + sdf.format(FutureDate);
        System.out.println("query= " + query);
        SearchSQL sql = new SearchSQL(query);
        SearchScope search = new SearchScope(store);
        FolderSet folders = (FolderSet) search.fetchObjects(sql, null, null, Boolean.valueOf(true));
        Iterator it = folders.iterator();


        while (it.hasNext()) {
            Folder folder = (Folder) it.next();
            String SectorFinOrg = folder.getProperties().getStringValue("SectorFinOrg");
            //  System.out.println("SectorFinOrg= " + SectorFinOrg);



            //MESSAGE TEXT
            Id FinInspectDocId = folder.get_Id();
            String subject = "Уведомление по плану мероприятий";
            String Org = folder.getProperties().getStringValue("Org");
            String body = "ВНИМАНИЕ! Касательно проверки «";
            body = body + Org + "»." + " Осталось 2 дня до наступления даты предоставления Плана мероприятий.";
            System.out.println(body);

            System.out.println("===========");
            System.out.println("RKKDate DateSubmPlan= " + folder.getProperties().getDateTimeValue("DateSubmPlan"));
            Date RKKDate = folder.getProperties().getDateTimeValue("DateSubmPlan");



            System.out.println("CURRENT DATE is " + date);

            c.setTime(date);
            c.add(Calendar.DATE, +2);
            Date DublicateDate = c.getTime();
            System.out.println("DublicateDate is " + DublicateDate);

            //if (date.after(PastDate) && (date.before(FutureDate)) && date.getDate() != PastDate.getDate() && date.getDate() != FutureDate.getDate()) {
            if (DublicateDate.getDate() == RKKDate.getDate() && DublicateDate.getMonth() == RKKDate.getMonth() && DublicateDate.getYear() == RKKDate.getYear()) {

                System.out.println("send notification");
                System.out.println("Current Date= " + date);


                String query2 = "select * from NoticeInfo where SectorFinOrg = " + "'" + SectorFinOrg + "'";
                System.out.println("query2= " + query2);
                SearchSQL sql2 = new SearchSQL(query2);
                SearchScope search2 = new SearchScope(store);
                DocumentSet documents = (DocumentSet) search2.fetchObjects(sql2, null, null, Boolean.valueOf(true));


                Iterator it2 = documents.iterator();
                Document doc2 = (Document) it2.next();
                System.out.println("DocumentTitle= " + doc2.getProperties().getStringValue("DocumentTitle"));
                EmailList = doc2.getProperties().getStringListValue("email");
                Iterator it3 = EmailList.iterator();

                while (it3.hasNext()) {

                    String email = (String) it3.next();
                    System.out.println("email= " + email);

                    // System.out.println("User= " + User);
//                    try {
//                        lotus.domino.Session dominoSession = lotus.domino.NotesFactory.createSession(dominoServer, dominoUsername, dominoPassword);
//                        lotus.domino.Database dominoDb = dominoSession.getDatabase(dominoServer, dominoMailbox);
//                        System.out.println("Connected as: " + dominoSession.getUserName());
//
//                        lotus.domino.Document memo = dominoDb.createDocument();
//                        memo.appendItemValue("Form", "Memo");
//                        memo.appendItemValue("Importance", "1");
//                        memo.appendItemValue("Subject", Subject);
//                        memo.appendItemValue("Body", Body);
//                        //memo.send(false, email + "/BSBNB@bsbnb");
//                        memo.send(false, email);
//
//
//                        dominoDb.recycle();
//                        dominoSession.recycle();
//                    } catch (NotesException e) {
//                        System.out.println("Error - " + e.id + " " + e.text);
//
//                    } catch (Exception e) {
//                        System.out.println("Error - " + e.toString());
//
//                    }


                }

                // NOTIFICATION STAFF
                String query3 = "select * from StaffInspectors where FinInspectDocId = " + "'" + FinInspectDocId + "'" + "and IsRuk= " + "'Руководитель'" + " and EndDate>" + sdf.format(date);
                //String query3 = "select * from StaffInspectors where FinInspectDocId = '{B59A6664-99EF-4C8C-BBF4-AB60718DBF6F}'";
                System.out.println("query3=" + query3);
                SearchSQL sql3 = new SearchSQL(query3);
                SearchScope search3 = new SearchScope(store);
                DocumentSet StaffInspectors = (DocumentSet) search3.fetchObjects(sql3, null, null, Boolean.valueOf(true));
                Iterator it4 = StaffInspectors.iterator();

                while (it4.hasNext()) {
                    Document doc3 = (Document) it4.next();
                    System.out.println("FIO= " + doc3.getProperties().getStringValue("FIO"));
                    System.out.println("Email= " + doc3.getProperties().getStringValue("EmailLotus"));
                    String email = doc3.getProperties().getStringValue("EmailLotus");
                    sendmail.SendMail(email, subject, body, dominoServer, dominoMailbox, dominoUsername, dominoPassword);
                }




            } else {
                System.out.println("no notification");
            }


        }


        //  System.out.println("DateRefItogFO= " + folder.getProperties().getDateTimeValue("DateRefItogFO"));


    }

    public ObjectStore getP8Connection() {
      	 // The connection URI includes the transport protocol (connection type),
        // host name, and port number that are used for server communication
        // Note these are the default P8 configuration parameters
        // String uri = "http://10.10.112.18:9080/wsi/FNCEWS40MTOM";
        String uri = "http://testcontent.corp.nb.rk:9080/wsi/FNCEWS40MTOM";
        // String uri = "http://10.10.112.170:9080/wsi/FNCEWS40MTOM";
        // Set the user id and password for authentication
        String username = "testinsadmin";
        String password = "Qwerty123";
        // Get the connection
        Connection conn = Factory.Connection.getConnection(uri);
        // The next 3 lines authenticate with the application server using the JAAS API
        Subject subject = UserContext.createSubject(conn, username, password, null);
        UserContext uc = UserContext.get();
        uc.pushSubject(subject);
        // Retrieve the specific Domain Object P8demodom
        Domain domain = Factory.Domain.fetchInstance(conn, "p8dom", null);
        System.out.println("Domain Name is: " + domain.get_Name());
        // Get the specific object store EVTFS
        ObjectStore store =
                Factory.ObjectStore.fetchInstance(domain, "FNOSINS", null);
        System.out.println("Objectstore is: " + store.get_Name());
        // Return the Object Store
        return store;
    }
}
