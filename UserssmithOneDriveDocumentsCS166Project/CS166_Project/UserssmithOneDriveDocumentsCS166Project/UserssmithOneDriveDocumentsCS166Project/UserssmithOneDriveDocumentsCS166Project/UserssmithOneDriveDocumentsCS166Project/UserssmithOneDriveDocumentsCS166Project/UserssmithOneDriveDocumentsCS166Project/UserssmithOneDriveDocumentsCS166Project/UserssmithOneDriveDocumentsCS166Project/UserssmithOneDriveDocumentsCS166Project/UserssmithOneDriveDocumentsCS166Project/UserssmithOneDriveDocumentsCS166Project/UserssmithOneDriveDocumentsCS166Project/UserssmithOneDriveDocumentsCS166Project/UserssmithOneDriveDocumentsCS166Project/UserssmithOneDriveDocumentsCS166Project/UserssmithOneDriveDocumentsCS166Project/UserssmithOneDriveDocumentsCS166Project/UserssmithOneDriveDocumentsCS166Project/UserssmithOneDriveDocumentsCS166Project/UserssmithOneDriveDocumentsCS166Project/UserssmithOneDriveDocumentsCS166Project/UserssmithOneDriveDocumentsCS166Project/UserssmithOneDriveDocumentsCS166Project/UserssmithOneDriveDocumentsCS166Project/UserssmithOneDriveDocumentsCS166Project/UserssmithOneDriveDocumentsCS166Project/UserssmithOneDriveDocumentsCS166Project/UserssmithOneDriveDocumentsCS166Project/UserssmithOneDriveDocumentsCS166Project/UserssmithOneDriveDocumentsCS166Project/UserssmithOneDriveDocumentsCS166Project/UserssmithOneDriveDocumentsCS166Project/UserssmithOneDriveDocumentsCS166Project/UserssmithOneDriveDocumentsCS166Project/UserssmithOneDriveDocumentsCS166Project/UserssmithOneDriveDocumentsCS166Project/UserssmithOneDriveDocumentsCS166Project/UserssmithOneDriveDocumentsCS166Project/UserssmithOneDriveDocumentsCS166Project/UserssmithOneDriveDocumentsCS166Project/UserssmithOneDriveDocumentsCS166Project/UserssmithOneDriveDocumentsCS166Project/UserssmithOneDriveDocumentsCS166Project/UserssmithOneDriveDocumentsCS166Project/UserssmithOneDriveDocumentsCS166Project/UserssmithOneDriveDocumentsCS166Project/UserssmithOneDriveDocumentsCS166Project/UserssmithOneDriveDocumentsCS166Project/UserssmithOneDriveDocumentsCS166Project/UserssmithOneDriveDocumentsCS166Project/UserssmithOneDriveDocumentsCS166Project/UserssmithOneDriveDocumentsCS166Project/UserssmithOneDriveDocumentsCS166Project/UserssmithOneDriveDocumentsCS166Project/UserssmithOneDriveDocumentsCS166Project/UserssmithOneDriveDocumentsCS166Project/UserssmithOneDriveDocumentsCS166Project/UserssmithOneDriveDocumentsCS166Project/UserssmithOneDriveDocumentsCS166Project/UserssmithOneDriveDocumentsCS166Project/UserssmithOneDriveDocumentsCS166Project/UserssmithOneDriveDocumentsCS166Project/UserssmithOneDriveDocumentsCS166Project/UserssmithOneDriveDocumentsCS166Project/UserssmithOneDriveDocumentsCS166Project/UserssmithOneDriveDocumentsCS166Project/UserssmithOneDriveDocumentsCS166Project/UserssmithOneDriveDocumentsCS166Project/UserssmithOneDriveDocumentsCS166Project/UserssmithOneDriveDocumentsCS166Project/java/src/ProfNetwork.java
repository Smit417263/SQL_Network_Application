/*
 * Smit Patel - 862076143
 * Gabrielle John - 862104371
 *
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */


public class ProfNetwork {
	public static String userNumber;
	public static int messageID = 27812;
	   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of ProfNetwork
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public ProfNetwork (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end ProfNetwork

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
          List<String> record = new ArrayList<String>();
         for (int i=1; i<=numCol; ++i)
            record.add(rs.getString (i));
         result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       if(rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
	 

      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            ProfNetwork.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      ProfNetwork  esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the ProfNetwrok object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new ProfNetwork (dbname, dbport, user, "");

         boolean keepon = true;
	    while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Goto Friend List");
                System.out.println("2. Update Profile");
                System.out.println("3. Write a new message");
                System.out.println("4. Send Friend Request");
		System.out.println("5. View Messages Recieved");
                System.out.println("6. View Messages Sent");
		System.out.println("7. Search for People");
		System.out.println("8. View Friend Request");
		System.out.println("10. View Profile");
		System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()){
                     case 1: FriendList(esql); break;
                     case 2: UpdateProfile(esql); break;
                     case 3: NewMessage(esql); break;
                     case 4: SendRequest(esql); break;
                     case 5: ViewMessagesReceived(esql); break;
		     case 6: ViewMessagesSent(esql); break;
		     case 7: UserSearch(esql); break;
		     case 8: ViewRequests(esql); break;
		     case 10: ViewProfile(esql); break;
                   case 9: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(ProfNetwork esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user email: ");
         String email = in.readLine();

	 //Creating empty contact\block lists for a user
	 String query = String.format("INSERT INTO USR (userId, password, email) VALUES ('%s','%s','%s')", login, password, email);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end

   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(ProfNetwork esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

	 ProfNetwork.userNumber = login;
	 //System.out.printf("\tOUTPUTTING userNumber:  '%s'", ProfNetwork.userNumber);

         String query = String.format("SELECT * FROM USR WHERE userId = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return login;
	 System.out.println("Incorrect user login or password.");
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

   public static void UpdateProfile(ProfNetwork esql) {
      try {
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter new password: ");
         String newPassword = in.readLine();

         String query = String.format("UPDATE USR SET password = '%s' WHERE userId = '%s'", newPassword, login);
         esql.executeUpdate(query);
         System.out.println ("Password changed successfully");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }



   public static void FriendList(ProfNetwork esql) {
      try {
         String query = String.format("SELECT CONNECTION_USR.userId, CONNECTION_USR.connectionId FROM CONNECTION_USR WHERE (userId = '%s' OR connectionId = '%s') AND status = 'Accept'", ProfNetwork.userNumber, ProfNetwork.userNumber);
         int userNum = esql.executeQueryAndPrintResult(query); //information about the user that is currently logged in
         String gloUserId = ProfNetwork.userNumber; //sender of the message


         System.out.print("Would you like to view their friends' profile? (y/n) ");
         String answer = in.readLine();

         while (!("y".equals(answer)) && !("n".equals(answer))) { //input error checking
            System.out.println("\tInput not valid");
            System.out.print("\tWould you like to view their friends' profile? (y/n) ");
            answer = in.readLine();
         }

         if("n".equals(answer)) {
            return;
         }

         System.out.print("Enter the userId of the profile you want to view: ");
         String userInput = in.readLine(); 
         FriendProfile(esql, userInput); 
         String input = "y"; //goes into while loops
         String usersId = userInput; //usersId should equal Lue.Yost



            while(!("n".equals(input))) {
               
               System.out.print("Would you like to send a message to this user? (y/n) ");
               String newMessageInput = in.readLine();
               while (!("y".equals(newMessageInput)) && !("n".equals(newMessageInput))) { //input error checking
                  System.out.println("\tInput not valid");
                  System.out.print("\tWould you like to send a message to this user? (y/n) ");
                  newMessageInput = in.readLine();
               }
               if("y".equals(newMessageInput)) {
                  NewMessageHelper(esql, usersId, gloUserId);
               }

               System.out.print("Would you like to view this user's friends? (y/n) "); //begins looking at Lue's friends (should ONLY be celine)
               input = in.readLine();

               while (!("y".equals(input)) && !("n".equals(input))) { //input error checking
                  System.out.println("\tInput not valid");
                  System.out.print("\tWould you like to view this user's friends? (y/n) ");
                  input = in.readLine();
               }

               if("y".equals(input)) {
                  FriendListHelper(esql, usersId); //should print only Lue's friends
                  System.out.print("\tEnter the userId's profile that you want to view: ");
                  usersId = in.readLine();
                  FriendProfile(esql, usersId);
                 // right here is where we add the connection request helper and check
                 boolean ACR = allowConnectionRequest(esql, gloUserId, usersId); 
		 if(ACR){
			System.out.print("\tWould you like to send a connection request to this user? (y/n) ");
			String newConnection = in.readLine();
	                while (!("y".equals(newConnection)) && !("n".equals(newConnection))) { //input error checking
         		         System.out.println("\tInput not valid");
                  		 System.out.print("\tWould you like to send a connection request to this uesr? (y/n ");
                 		 newConnection = in.readLine();
               		}
               		if("y".equals(newConnection)) {
               		  String q2 = String.format("INSERT INTO CONNECTION_USR (userId, connectionId, status) VALUES ('%s', '%s', 'Request')", usersId, gloUserId);
			  esql.executeUpdate(q2);
			  System.out.println("Connection request has been sent!");
			}

	
		 }

               }
            }

      }catch(Exception e){
         System.err.println (e.getMessage ());
         return;
      }
   }







   public static void FriendListHelper(ProfNetwork esql, String friendId) {
      try {
         String query = String.format("SELECT CONNECTION_USR.userId, CONNECTION_USR.connectionId FROM CONNECTION_USR WHERE (userId = '%s' OR connectionId = '%s') AND status = 'Accept'", friendId, friendId);
         int userNum = esql.executeQueryAndPrintResult(query);

      }catch(Exception e){
         System.err.println (e.getMessage ());
         return;
      }

   }



    public static void NewMessage(ProfNetwork esql) {
      try {
         System.out.print("\tEnter a new message: ");
         String message = in.readLine();
         System.out.print("\tWho would you like to send the message to? ");
         String name = in.readLine();

         String query = String.format("SELECT USR.name FROM USR WHERE USR.name = '%s'", name);
         int userNum = esql.executeQueryAndPrintResult(query);

         if (userNum > 0) {
            String query2 = String.format("SELECT USR.userId FROM USR WHERE USR.name = '%s'", name);
            List<List<String>> receiverID = esql.executeQueryAndReturnResult(query2);
            ProfNetwork.messageID = ProfNetwork.messageID + 1;
            String newMesssageQuery = String.format("INSERT INTO MESSAGE (msgId, senderId, receiverId, contents, status) VALUES ('%d', '%s', '%s', '%s', 'Delivered')", ProfNetwork.messageID, ProfNetwork.userNumber, receiverID.get(0).get(0), message);
            esql.executeUpdate(newMesssageQuery);
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }



   public static void NewMessageHelper(ProfNetwork esql, String receiver, String sender) {
      try {
         System.out.print("\tEnter a new message: ");
         String message = in.readLine();

         String query = String.format("SELECT USR.userId FROM USR WHERE USR.userId = '%s'", receiver);
         int userNum = esql.executeQuery(query);

         if (userNum > 0) {
            ProfNetwork.messageID = ProfNetwork.messageID + 1;
            String newMesssageQuery = String.format("INSERT INTO MESSAGE (msgId, senderId, receiverId, contents, status) VALUES ('%d', '%s', '%s', '%s', 'Delivered')", ProfNetwork.messageID, sender, receiver, message);
            esql.executeUpdate(newMesssageQuery);
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }



   public static String ViewMessagesReceived(ProfNetwork esql) {
      try {
         String receivedMessages = "";
         String query = String.format("SELECT MESSAGE.msgId,MESSAGE.contents FROM MESSAGE WHERE receiverId = '%s'", ProfNetwork.userNumber);
         int userNum = esql.executeQueryAndPrintResult(query);

	 System.out.print("\tWould you like to delete a message? (y/n) ");
         String userInput = in.readLine();

	 while (!("y".equals(userInput)) &&  !("n".equals(userInput))){
            System.out.println("\tInput not valid");
            System.out.print("\tWould you like to delete a message? (y/n) ");
            userInput = in.readLine();
         }
         if ("y".equals(userInput)) {
            System.out.print("\tWhich msgId would you like to delete? ");
            String deleteMessage = in.readLine();
            String query2 = String.format("UPDATE MESSAGE SET receiverId = '' WHERE msgId = '%s'", deleteMessage);
            esql.executeUpdate(query2);
            System.out.println("\tMessage deleted successfully!");
         }





         if (userNum > 0)
              return receivedMessages;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }

   public static String ViewMessagesSent(ProfNetwork esql) {
      try {
         String sentMessages = "";
         String query = String.format("SELECT MESSAGE.msgId, MESSAGE.contents FROM MESSAGE WHERE senderId = '%s'", ProfNetwork.userNumber);
         int userNum = esql.executeQueryAndPrintResult(query);


	 System.out.print("\tWould you like to delete a message? (y/n) ");
         String userInput = in.readLine();

         while (!("y".equals(userInput)) && !("n".equals(userInput))) {
            System.out.println("\tInput not valid");
            System.out.print("\tWould you like to delete a message? (y/n) ");
            userInput = in.readLine();
         }
         if ("y".equals(userInput)) {
            System.out.print("\tWhich msgId would you like to delete? ");
            String deleteMessage = in.readLine();
            String query2 = String.format("UPDATE MESSAGE SET senderId = '' WHERE msgId = '%s'", deleteMessage);
            esql.executeUpdate(query2);
            System.out.println("\tMessage deleted successfully!");
         }


         if (userNum > 0)
              return sentMessages;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }



   public static String UserSearch(ProfNetwork esql) {
      try{
         System.out.print("\tSearch for a user by name: ");
         String search = in.readLine();

         String query = String.format("SELECT USR.name FROM USR WHERE USR.name = '%s'", search);
         int userNum = esql.executeQueryAndPrintResult(query);
         if (userNum > 0)
              return search;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }

   }


   public static void ViewRequests(ProfNetwork esql) {
      try {
         String query = String.format("SELECT CONNECTION_USR FROM CONNECTION_USR WHERE userId = '%s' AND status = 'Request'", ProfNetwork.userNumber);
         int userNum = esql.executeQueryAndPrintResult(query);
         System.out.print("\tWould you like to accept or reject a request? (y/n)");
         String userInput = in.readLine();
	
	if(userNum <=  0){
	 return;
	}
	
         while (!("y".equals(userInput)) && !("n".equals(userInput))) {
            System.out.println("\tInput not valid");
            System.out.print("\tWould you like to accept or reject a request? (y/n) ");
            userInput = in.readLine();
         }

         if("y".equals(userInput)) {
            System.out.print("\tSelect a request by entering the userId: ");
            String usersId = in.readLine();
            System.out.print("\tWould you like to accept or reject this request? (a/r) ");
            String userAnswer = in.readLine();

            while (!("a".equals(userAnswer)) && !("r".equals(userAnswer))) {
               System.out.println("\tInput not valid");
               System.out.print("\tWould you like to accept or reject this request? (a/r) ");
               userAnswer = in.readLine();
            }

            if ("a".equals(userAnswer)) {
               String query2 = String.format("UPDATE CONNECTION_USR SET status = 'Accept' WHERE userId = '%s' AND connectionId = '%s'", ProfNetwork.userNumber, usersId);
               esql.executeUpdate(query2);
               System.out.println("\tSuccessfully accepted friend request!");
            }
            else {
               String query3 = String.format("UPDATE CONNECTION_USR SET status = 'Reject' WHERE userId = '%s' AND connectionId = '%s'", ProfNetwork.userNumber, usersId);
               esql.executeUpdate(query3);
               System.out.println("\tSuccessfully rejected friend request");
            }
         }
         if (userNum > 0)
            return;
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }





    public static void ViewProfile(ProfNetwork esql) {
      try {
         String query = String.format("SELECT USR.name, USR.email, USR.dateOfBirth FROM USR WHERE userId = '%s'", ProfNetwork.userNumber);
         System.out.println("User Information\n");
         int userInfo = esql.executeQueryAndPrintResult(query);
         String query2 = String.format("SELECT WORK_EXPR.company, WORK_EXPR.role FROM WORK_EXPR WHERE userId = '%s'", ProfNetwork.userNumber);
         System.out.println("User Work Experience\n");
         int workInfo = esql.executeQueryAndPrintResult(query2);
         String query3 = String.format("SELECT EDUCATIONAL_DETAILS.instituitionName, EDUCATIONAL_DETAILS.major, EDUCATIONAL_DETAILS.degree FROM EDUCATIONAL_DETAILS WHERE userId = '%s'", ProfNetwork.userNumber);
         System.out.println("User Educational Details\n");
         int schoolInfo = esql.executeQueryAndPrintResult(query3);
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }


   public static void FriendProfile(ProfNetwork esql, String friendInfo) {
      try {
         String query = String.format("SELECT USR.name, USR.email, USR.dateOfBirth FROM USR WHERE userId = '%s'", friendInfo);
         System.out.println("User Information");
         int userInfo = esql.executeQueryAndPrintResult(query);
         String query2 = String.format("SELECT WORK_EXPR.company, WORK_EXPR.role FROM WORK_EXPR WHERE userId = '%s'", friendInfo);
         System.out.println("User Work Experience");
         int workInfo = esql.executeQueryAndPrintResult(query2);
         String query3 = String.format("SELECT EDUCATIONAL_DETAILS.instituitionName, EDUCATIONAL_DETAILS.major, EDUCATIONAL_DETAILS.degree FROM EDUCATIONAL_DETAILS WHERE userId = '%s'", friendInfo);
         System.out.println("User Educational Details");
         int schoolInfo = esql.executeQueryAndPrintResult(query3);
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }




public static List<String> SendRequestHelper(ProfNetwork esql, String userInput) {
         try {
            List<List<String>> friendList = new ArrayList<List<String>>();
            List<String> possibileConnections = new ArrayList<String>();
	    String query = String.format("SELECT userId FROM CONNECTION_USR WHERE status = 'Accept' AND connectionId = '%s'", userInput);
            List<List<String>> first_one = esql.executeQueryAndReturnResult(query);
            String query2 = String.format("SELECT connectionId FROM CONNECTION_USR WHERE status = 'Accept' AND userId = '%s'", userInput);
            List<List<String>> first_two = esql.executeQueryAndReturnResult(query2);
            friendList.addAll(first_one);
            friendList.addAll(first_two);

            List<List<String>> secondLevel = new ArrayList<List<String>>();
	
		
	   int first_level_size = friendList.size();
	

            for(int i = 0; i < first_level_size; i++) {
               for (int j = 0; j < friendList.get(i).size(); j++) {
                  String iterator = friendList.get(i).get(j);
                  String query3 = String.format("SELECT userId FROM CONNECTION_USR WHERE status = 'Accept' AND connectionId = '%s'", iterator);
                  List<List<String>> second_one = esql.executeQueryAndReturnResult(query3);
                  String query4 = String.format("SELECT connectionId FROM CONNECTION_USR WHERE status = 'Accept' AND userId = '%s'", iterator);
                  List<List<String>> second_two = esql.executeQueryAndReturnResult(query4);
                  friendList.addAll(second_one);
                  friendList.addAll(second_two);
                  secondLevel.addAll(second_one);
                  secondLevel.addAll(second_two);
	   //  		System.out.println();
               }
            }
            
	    for(int k = 0; k < secondLevel.size(); k++) {
               for(int x = 0; x < secondLevel.get(k).size(); x++) {
                  String iterator = secondLevel.get(k).get(x);
                  String query3 = String.format("SELECT userId FROM CONNECTION_USR WHERE status = 'Accept' AND connectionId = '%s'", iterator);
                  List<List<String>> second_one = esql.executeQueryAndReturnResult(query3);
                  String query4 = String.format("SELECT connectionId FROM CONNECTION_USR WHERE status = 'Accept' AND userId = '%s'", iterator);
                  List<List<String>> second_two = esql.executeQueryAndReturnResult(query4);
                  friendList.addAll(second_one);
                  friendList.addAll(second_two);
               }
            }

	    for(int k = 0; k < friendList.size(); k++) {
               for(int x = 0; x < friendList.get(k).size(); x++) {
//                  System.out.println(friendList.get(k).get(x));
                  possibileConnections.add(friendList.get(k).get(x));
               }
            }
		
	  return possibileConnections;

         }
         catch(Exception e){
            System.err.println (e.getMessage ());
	    return null;
         }
   }







  public static void SendRequest(ProfNetwork esql) {
      try {
         String query = String.format("SELECT CONNECTION_USR.userId, CONNECTION_USR.connectionId FROM CONNECTION_USR WHERE (userId = '%s' OR connectionId = '%s') AND status = 'Accept'", ProfNetwork.userNumber, ProfNetwork.userNumber);
         int userNum = esql.executeQuery(query);

         if(userNum == 0) {
            int newConnections = 0;
            while(newConnections < 5) {
               System.out.print("Input the userId of the person you want to send a friend request to: ");
               String friend = in.readLine();
               String query2 = String.format("INSERT INTO CONNECTION_USR (userId, connectionId, status) VALUES ('%s', '%s', 'Request')", friend, ProfNetwork.userNumber);
	       esql.executeUpdate(query2);
               newConnections++;

            }
         }
	else{
		 List<String> possibileConnections = SendRequestHelper(esql, ProfNetwork.userNumber);
		 List<String> firstLevel =  firstLevelConnection(esql, ProfNetwork.userNumber);
		 ArrayList<String> no_dup = new ArrayList<String>();

                for (String element : possibileConnections) {
                	if (!no_dup.contains(element)) {
                               no_dup.add(element);
                	}
                }
		
	
		String tmp = ProfNetwork.userNumber;
		int sz = 30 - tmp.length();
		for(int i = 0; i < sz; i++){
			tmp = tmp + " ";
		}
		
		firstLevel.add(tmp);
	

		no_dup.removeAll(firstLevel);

	
	     	System.out.println();  
		System.out.println(no_dup);	
	
		System.out.print("Enter the userId of the person you would like to send a friend request to: ");
                String request = in.readLine();
                String query3 = String.format("INSERT INTO CONNECTION_USR (userId, connectionId, status) VALUES ('%s', '%s', 'Request')", request, ProfNetwork.userNumber);
                esql.executeUpdate(query3);
		System.out.println("Your friend request has been sent!");
        		
		
		
	}

		

	 

      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }



public static List<String> firstLevelConnection(ProfNetwork esql, String userInput) {


      try {
         List<List<String>> friendList = new ArrayList<List<String>>();
            List<String> possibileConnections = new ArrayList<String>();
            String query = String.format("SELECT userId FROM CONNECTION_USR WHERE status = 'Accept' AND connectionId = '%s'", userInput);
            List<List<String>> first_one = esql.executeQueryAndReturnResult(query);
            String query2 = String.format("SELECT connectionId FROM CONNECTION_USR WHERE status = 'Accept' AND userId = '%s'", userInput);
            List<List<String>> first_two = esql.executeQueryAndReturnResult(query2);
            friendList.addAll(first_one);
            friendList.addAll(first_two);



            for(int k = 0; k < friendList.size(); k++) {
               for(int x = 0; x < friendList.get(k).size(); x++) {
                //  System.out.println(friendList.get(k).get(x));
                  possibileConnections.add(friendList.get(k).get(x));
               }
            }
            return possibileConnections;
      }catch(Exception e){
            System.err.println (e.getMessage ());
            return null;
         }
   }

public static boolean allowConnectionRequest(ProfNetwork esql, String userInput, String receiver){

		 List<String> possibileConnections = SendRequestHelper(esql, ProfNetwork.userNumber);
		 List<String> firstLevel =  firstLevelConnection(esql, ProfNetwork.userNumber);
//	    	System.out.println("HERE  RIGHT   HERE");  
//		for(int i=0;i<possibileConnections.size();i++){
//  	  		System.out.println(possibileConnections.get(i));
//		}
		ArrayList<String> no_dup = new ArrayList<String>();

                for (String element : possibileConnections) {
                	if (!no_dup.contains(element)) {
                               no_dup.add(element);
                	}
                }
  //          	System.out.println("HERE  RIGHT   HERE");  
//		for(int i=0;i<no_dup.size();i++){
  //	  		System.out.println(no_dup.get(i));
//		}
		
	
		String tmp = ProfNetwork.userNumber;
		int sz = 30 - tmp.length();
		for(int i = 0; i < sz; i++){
			tmp = tmp + " ";
		}
		
		firstLevel.add(tmp);
//	     	System.out.println("HERE  RIGHT   HERE");  
//  	  	System.out.println(firstLevel);
	

		no_dup.removeAll(firstLevel);
	
//	     	System.out.println();  
//		System.out.println(no_dup);	

		String compare_val = receiver;
		sz = 30 - compare_val.length();
		for(int i = 0; i < sz; i++){
			compare_val = compare_val + " ";
		}
		boolean can_send = false;
		for(int i = 0; i < no_dup.size(); i++){
			if(compare_val.equals(no_dup.get(i))){
				can_send = true;
			}
		}
		
	return can_send;	

	}


}
//end ProfNetwork
