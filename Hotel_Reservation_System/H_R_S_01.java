import java.beans.PropertyEditorSupport;
import java.sql.*;
import java.util.Scanner;

public class H_R_S_01 {
    public static final String url = "jdbc:mysql://localhost:3306/hotel_reservation_system";
    public static final String user = "root";
    public static final String psswrd = "shvbhratri.me#1";


    public static void main(String[] args) {

        try {
            //STEP-1. Load JDBC Driver (in try catch)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            //STEP 2.  Create the connection object
            Connection con = DriverManager.getConnection(url, user, psswrd);

            while (true) {
                System.out.println();
                System.out.println("....Welcome to Hotel Reservation System....");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Book Room");
                System.out.println("2. View Bookings");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Booking");
                System.out.println("5. Cancel Booking");
                System.out.println("0. Exit");
                System.out.println("Enter Choice: ");

                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        BookRoom(con, sc);
                        break;
                    case 2:
                        FetchBooking(con, sc);
                        break;
                    case 3:
                        get_room_no(con, sc);
                        break;
                    case 4:
                        update_booking(con, sc);
                        break;
                    case 5:
                        cancel_booking(con, sc);
                        break;
                    case 0:
                        System.out.println("Exiting...");
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid Choice, Try Again");

                        break;


                }
            }
        } catch (SQLException s) {
            System.out.println(s.getMessage());
        }
    }


    static void BookRoom(Connection con, Scanner sc) {

        String book_room = String.format(" INSERT INTO RESERVATION(reservation_id, guest_name,room_number,contact_number,reservation_date)" +
                "VALUES(?,?,?,?,NOW()) ");
        try {
            PreparedStatement preparedStatement = con.prepareStatement(book_room);
            System.out.println("Enter Guest Name: ");
            sc.nextLine();
            String guest_name = sc.nextLine();
            System.out.println("Enter room_number: ");
            int room_number = sc.nextInt();
            System.out.println("Enter Contact Number: ");
            String contact_number = sc.next();

            preparedStatement.setInt(1, 0);
            preparedStatement.setString(2, guest_name);
            preparedStatement.setInt(3, room_number);
            preparedStatement.setString(4, contact_number);
            preparedStatement.addBatch();
            preparedStatement.executeBatch();

            System.out.println("Room Number " + room_number + " Booked Successfully...");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    static void FetchBooking(Connection con, Scanner sc) {

        String fetchbooking = String.format("SELECT * FROM reservation WHERE reservation_id =?");

        try {
            PreparedStatement preparedStatement = con.prepareStatement(fetchbooking);
            System.out.println("Enter Reservation ID: ");
            int fetch_input = sc.nextInt();
            preparedStatement.setInt(1, fetch_input);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int r_id = resultSet.getInt("reservation_id");
                String g_name = resultSet.getString("guest_name");
                int r_number = resultSet.getInt("room_number");
                String c_number = resultSet.getString("contact_number");
                Timestamp b_date = resultSet.getTimestamp("reservation_date");

                System.out.println("Reservation ID      -> " + r_id + "\nGuest Name          -> " + g_name +
                        "\nRoom Number         -> " + r_number + "\nContact Number      -> " + c_number + "\nBooking Date & Time -> " + b_date);

            } else {
                System.out.println("No Booking Found For Reservation ID " + fetch_input);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }


    static void get_room_no(Connection con, Scanner sc) {

        String grn_query = String.format("SELECT reservation_id, room_number FROM reservation WHERE guest_name = ?;");

        try {
            PreparedStatement preparedStatement = con.prepareStatement(grn_query);
            System.out.println("Enter Guest Name: ");
            sc.nextLine();
            String input_name = sc.nextLine();
            preparedStatement.setString(1, input_name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int room_number = resultSet.getInt("room_number");
                int res_id = resultSet.getInt("reservation_id");
                System.out.println("hello " + input_name + "...\nyour room number is " + room_number + " & reservation id is " + res_id);
            } else {
                System.out.println("no booking foung for guest " + input_name);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    static void update_booking(Connection con, Scanner sc) {

        String find_reservation = "SELECT * FROM reservation WHERE reservation_id =?";
        String update_booking = String.format("UPDATE reservation SET guest_name = ?, room_number = ?, contact_number = ?  WHERE reservation_id=? ");


        try {
            PreparedStatement preparedStatement = con.prepareStatement(find_reservation);
            System.out.println("Enter Reservation ID to Update Booking: ");
            int res_id_input = sc.nextInt();

            preparedStatement.setInt(1, res_id_input);
            ResultSet resultSet = preparedStatement.executeQuery();


            if (resultSet.next()) {
                System.out.println("Enter New Guest Name: ");
                sc.nextLine();
                String new_guest_name = sc.nextLine();
                System.out.println("Enter New Room Number: ");
                int new_room_number = sc.nextInt();
                System.out.println("Enter New Contact Number: ");
                String new_contact_number = sc.next();

                PreparedStatement updatePreparedStatement = con.prepareStatement(update_booking);
                updatePreparedStatement.setString(1, new_guest_name);
                updatePreparedStatement.setInt(2, new_room_number);
                updatePreparedStatement.setString(3, new_contact_number);
                updatePreparedStatement.setInt(4, res_id_input);

                int rows_affected = updatePreparedStatement.executeUpdate();
                if (rows_affected > 0) {
                    System.out.println("Your Booking Is Updated Successfully..");
                } else {
                    System.out.println("Failed to Update Booking..");
                }

            } else {
                System.out.println("Reservation ID " + res_id_input + " Does Not Exist..");
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void cancel_booking(Connection con, Scanner sc) {

        String d_booking_query = "DELETE FROM reservation WHERE reservation_id = ?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(d_booking_query);
            System.out.println("Enter Your Reservation ID: ");
            sc.nextLine();
            int input_id = sc.nextInt();
            preparedStatement.setInt(1, input_id);
            preparedStatement.executeUpdate();
            System.out.println("Booking Cancelled Successfully...");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

}




