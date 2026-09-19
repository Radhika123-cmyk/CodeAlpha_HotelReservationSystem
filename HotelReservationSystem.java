import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
 * Hotel Reservation System
 * MCA Level GUI Project
 * Java Swing + OOP + File I/O
 */

public class HotelReservationSystem extends JFrame {

    private final ArrayList<Room> rooms = new ArrayList<>();
    private final ArrayList<Reservation> reservations = new ArrayList<>();

    private final String FILE_NAME = "reservations.txt";
    private final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // GUI Components
    private JTextField guestNameField;
    private JTextField phoneField;
    private JTextField checkInField;
    private JTextField checkOutField;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> roomCombo;

    private JTextField searchCheckInField;
    private JTextField searchCheckOutField;
    private JComboBox<String> searchCategoryCombo;

    private JTable roomTable;
    private JTable reservationTable;

    private DefaultTableModel roomTableModel;
    private DefaultTableModel reservationTableModel;

    private JTextField cancelIdField;

    public HotelReservationSystem() {

        setTitle("Hotel Reservation System");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeRooms();
        loadReservations();

        createGUI();

        setVisible(true);
    }

    // =========================
    // ROOM INITIALIZATION
    // =========================

    private void initializeRooms() {

        rooms.add(new Room(101, "Standard", 2000));
        rooms.add(new Room(102, "Standard", 2000));
        rooms.add(new Room(103, "Standard", 2000));

        rooms.add(new Room(201, "Deluxe", 3500));
        rooms.add(new Room(202, "Deluxe", 3500));
        rooms.add(new Room(203, "Deluxe", 3500));

        rooms.add(new Room(301, "Suite", 5500));
        rooms.add(new Room(302, "Suite", 5500));
    }

    // =========================
    // GUI
    // =========================

    private void createGUI() {

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Search Rooms", createSearchPanel());
        tabs.addTab("Make Reservation", createReservationPanel());
        tabs.addTab("Manage Reservations", createManagePanel());
        tabs.addTab("Booking Details", createDetailsPanel());

        add(tabs);
    }

    // =========================
    // SEARCH PANEL
    // =========================

    private JPanel createSearchPanel() {

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel inputPanel = new JPanel(new GridLayout(2, 6, 10, 10));

        inputPanel.add(new JLabel("Check-in (YYYY-MM-DD):"));
        searchCheckInField = new JTextField();

        inputPanel.add(searchCheckInField);

        inputPanel.add(new JLabel("Check-out (YYYY-MM-DD):"));
        searchCheckOutField = new JTextField();

        inputPanel.add(searchCheckOutField);

        inputPanel.add(new JLabel("Room Category:"));

        searchCategoryCombo = new JComboBox<>(
                new String[]{"All", "Standard", "Deluxe", "Suite"}
        );

        inputPanel.add(searchCategoryCombo);

        JButton searchButton = new JButton("Search Available Rooms");

        inputPanel.add(searchButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        roomTableModel = new DefaultTableModel(
                new String[]{"Room No.", "Category", "Price/Night", "Status"},
                0
        );

        roomTable = new JTable(roomTableModel);

        mainPanel.add(new JScrollPane(roomTable), BorderLayout.CENTER);

        searchButton.addActionListener(e -> searchRooms());

        return mainPanel;
    }

    // =========================
    // RESERVATION PANEL
    // =========================

    private JPanel createReservationPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));

        form.add(new JLabel("Guest Name:"));
        guestNameField = new JTextField();
        form.add(guestNameField);

        form.add(new JLabel("Phone Number:"));
        phoneField = new JTextField();
        form.add(phoneField);

        form.add(new JLabel("Room Category:"));

        categoryCombo = new JComboBox<>(
                new String[]{"Standard", "Deluxe", "Suite"}
        );

        form.add(categoryCombo);

        form.add(new JLabel("Room Number:"));

        roomCombo = new JComboBox<>();
        form.add(roomCombo);

        form.add(new JLabel("Check-in (YYYY-MM-DD):"));
        checkInField = new JTextField();
        form.add(checkInField);

        form.add(new JLabel("Check-out (YYYY-MM-DD):"));
        checkOutField = new JTextField();
        form.add(checkOutField);

        panel.add(form, BorderLayout.NORTH);

        JButton loadRoomsButton = new JButton("Load Available Rooms");
        JButton bookButton = new JButton("Book Room");
        JButton clearButton = new JButton("Clear");

        JPanel buttons = new JPanel();

        buttons.add(loadRoomsButton);
        buttons.add(bookButton);
        buttons.add(clearButton);

        panel.add(buttons, BorderLayout.CENTER);

        categoryCombo.addActionListener(e -> loadAvailableRooms());

        loadRoomsButton.addActionListener(e -> loadAvailableRooms());

        bookButton.addActionListener(e -> makeReservation());

        clearButton.addActionListener(e -> clearReservationForm());

        return panel;
    }

    // =========================
    // MANAGE RESERVATIONS
    // =========================

    private JPanel createManagePanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        reservationTableModel = new DefaultTableModel(
                new String[]{
                        "Reservation ID",
                        "Guest",
                        "Phone",
                        "Room",
                        "Category",
                        "Check-in",
                        "Check-out",
                        "Amount",
                        "Status"
                },
                0
        );

        reservationTable = new JTable(reservationTableModel);

        panel.add(new JScrollPane(reservationTable), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh Reservations");

        JPanel bottom = new JPanel();

        bottom.add(refreshButton);

        bottom.add(new JLabel("Reservation ID:"));

        cancelIdField = new JTextField(12);
        bottom.add(cancelIdField);

        JButton cancelButton = new JButton("Cancel Reservation");

        bottom.add(cancelButton);

        panel.add(bottom, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> refreshReservationTable());

        cancelButton.addActionListener(e -> cancelReservation());

        refreshReservationTable();

        return panel;
    }

    // =========================
    // BOOKING DETAILS
    // =========================

    private JPanel createDetailsPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea detailsArea = new JTextArea();

        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JButton viewButton = new JButton("View Selected Booking");

        panel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        panel.add(viewButton, BorderLayout.SOUTH);

        viewButton.addActionListener(e -> {

            int selectedRow = reservationTable != null
                    ? reservationTable.getSelectedRow()
                    : -1;

            if (selectedRow == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please select a booking from Manage Reservations."
                );

                return;
            }

            String reservationId =
                    reservationTableModel.getValueAt(
                            selectedRow, 0
                    ).toString();

            Reservation reservation =
                    findReservation(reservationId);

            if (reservation != null) {

                detailsArea.setText(
                        reservation.getDetailedInformation()
                );
            }
        });

        return panel;
    }

    // =========================
    // SEARCH ROOMS
    // =========================

    private void searchRooms() {

        try {

            LocalDate checkIn =
                    LocalDate.parse(
                            searchCheckInField.getText().trim(),
                            DATE_FORMAT
                    );

            LocalDate checkOut =
                    LocalDate.parse(
                            searchCheckOutField.getText().trim(),
                            DATE_FORMAT
                    );

            if (!isValidDateRange(checkIn, checkOut)) {
                return;
            }

            String category =
                    searchCategoryCombo.getSelectedItem().toString();

            roomTableModel.setRowCount(0);

            for (Room room : rooms) {

                boolean categoryMatch =
                        category.equals("All")
                                || room.getCategory().equals(category);

                if (categoryMatch) {

                    boolean available =
                            isRoomAvailable(
                                    room.getRoomNumber(),
                                    checkIn,
                                    checkOut
                            );

                    roomTableModel.addRow(
                            new Object[]{
                                    room.getRoomNumber(),
                                    room.getCategory(),
                                    "₹" + room.getPricePerNight(),
                                    available ? "Available" : "Booked"
                            }
                    );
                }
            }

        } catch (DateTimeParseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter dates in YYYY-MM-DD format.\nExample: 2026-09-25"
            );
        }
    }

    // =========================
    // LOAD AVAILABLE ROOMS
    // =========================

    private void loadAvailableRooms() {

        roomCombo.removeAllItems();

        try {

            LocalDate checkIn =
                    LocalDate.parse(
                            checkInField.getText().trim(),
                            DATE_FORMAT
                    );

            LocalDate checkOut =
                    LocalDate.parse(
                            checkOutField.getText().trim(),
                            DATE_FORMAT
                    );

            if (!isValidDateRange(checkIn, checkOut)) {
                return;
            }

            String category =
                    categoryCombo.getSelectedItem().toString();

            for (Room room : rooms) {

                if (room.getCategory().equals(category)
                        && isRoomAvailable(
                        room.getRoomNumber(),
                        checkIn,
                        checkOut)) {

                    roomCombo.addItem(
                            String.valueOf(room.getRoomNumber())
                    );
                }
            }

            if (roomCombo.getItemCount() == 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "No rooms are available for the selected dates."
                );
            }

        } catch (DateTimeParseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid dates in YYYY-MM-DD format."
            );
        }
    }

    // =========================
    // MAKE RESERVATION
    // =========================

    private void makeReservation() {

        String guestName = guestNameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (guestName.isEmpty() || phone.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter guest name and phone number."
            );

            return;
        }

        if (roomCombo.getSelectedItem() == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please load and select an available room."
            );

            return;
        }

        try {

            LocalDate checkIn =
                    LocalDate.parse(
                            checkInField.getText().trim(),
                            DATE_FORMAT
                    );

            LocalDate checkOut =
                    LocalDate.parse(
                            checkOutField.getText().trim(),
                            DATE_FORMAT
                    );

            if (!isValidDateRange(checkIn, checkOut)) {
                return;
            }

            int roomNumber =
                    Integer.parseInt(
                            roomCombo.getSelectedItem().toString()
                    );

            Room room = findRoom(roomNumber);

            if (room == null) {
                return;
            }

            if (!isRoomAvailable(roomNumber, checkIn, checkOut)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Sorry! This room is already booked."
                );

                return;
            }

            long nights =
                    checkOut.toEpochDay() - checkIn.toEpochDay();

            double amount =
                    nights * room.getPricePerNight();

            int paymentChoice = JOptionPane.showConfirmDialog(
                    this,
                    "Room: " + roomNumber +
                            "\nCategory: " + room.getCategory() +
                            "\nNights: " + nights +
                            "\nTotal Amount: ₹" + amount +
                            "\n\nProceed with simulated payment?",
                    "Payment Confirmation",
                    JOptionPane.YES_NO_OPTION
            );

            if (paymentChoice != JOptionPane.YES_OPTION) {

                JOptionPane.showMessageDialog(
                        this,
                        "Payment cancelled. Booking not created."
                );

                return;
            }

            String paymentId =
                    "PAY-" +
                            UUID.randomUUID()
                                    .toString()
                                    .substring(0, 8)
                                    .toUpperCase();

            String reservationId =
                    "RES-" +
                            UUID.randomUUID()
                                    .toString()
                                    .substring(0, 8)
                                    .toUpperCase();

            Reservation reservation =
                    new Reservation(
                            reservationId,
                            guestName,
                            phone,
                            roomNumber,
                            room.getCategory(),
                            checkIn,
                            checkOut,
                            amount,
                            paymentId,
                            "Paid",
                            "Confirmed"
                    );

            reservations.add(reservation);

            saveReservations();

            JOptionPane.showMessageDialog(
                    this,
                    "Booking Successful!\n\n" +
                            "Reservation ID: " + reservationId +
                            "\nPayment ID: " + paymentId +
                            "\nRoom: " + roomNumber +
                            "\nAmount Paid: ₹" + amount,
                    "Reservation Confirmed",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearReservationForm();
            refreshReservationTable();

        } catch (DateTimeParseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid dates in YYYY-MM-DD format."
            );
        }
    }

    // =========================
    // CANCEL RESERVATION
    // =========================

    private void cancelReservation() {

        String id = cancelIdField.getText().trim();

        if (id.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter a reservation ID."
            );

            return;
        }

        Reservation reservation =
                findReservation(id);

        if (reservation == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Reservation not found."
            );

            return;
        }

        if (reservation.getStatus().equals("Cancelled")) {

            JOptionPane.showMessageDialog(
                    this,
                    "This reservation is already cancelled."
            );

            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Cancel reservation " + id + "?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {

            reservation.setStatus("Cancelled");

            saveReservations();
            refreshReservationTable();

            JOptionPane.showMessageDialog(
                    this,
                    "Reservation cancelled successfully."
            );
        }
    }

    // =========================
    // AVAILABILITY CHECK
    // =========================

    private boolean isRoomAvailable(
            int roomNumber,
            LocalDate newCheckIn,
            LocalDate newCheckOut) {

        for (Reservation r : reservations) {

            if (r.getRoomNumber() == roomNumber
                    && !r.getStatus().equals("Cancelled")) {

                boolean overlap =
                        newCheckIn.isBefore(r.getCheckOut())
                                && newCheckOut.isAfter(r.getCheckIn());

                if (overlap) {
                    return false;
                }
            }
        }

        return true;
    }

    // =========================
    // VALIDATE DATE
    // =========================

    private boolean isValidDateRange(
            LocalDate checkIn,
            LocalDate checkOut) {

        if (!checkOut.isAfter(checkIn)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Check-out date must be after check-in date."
            );

            return false;
        }

        return true;
    }

    // =========================
    // REFRESH TABLE
    // =========================

    private void refreshReservationTable() {

        if (reservationTableModel == null) {
            return;
        }

        reservationTableModel.setRowCount(0);

        for (Reservation r : reservations) {

            reservationTableModel.addRow(
                    new Object[]{
                            r.getReservationId(),
                            r.getGuestName(),
                            r.getPhone(),
                            r.getRoomNumber(),
                            r.getCategory(),
                            r.getCheckIn(),
                            r.getCheckOut(),
                            "₹" + r.getAmount(),
                            r.getStatus()
                    }
            );
        }
    }

    // =========================
    // CLEAR FORM
    // =========================

    private void clearReservationForm() {

        guestNameField.setText("");
        phoneField.setText("");
        checkInField.setText("");
        checkOutField.setText("");
        roomCombo.removeAllItems();
    }

    // =========================
    // FIND ROOM
    // =========================

    private Room findRoom(int roomNumber) {

        for (Room room : rooms) {

            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }

        return null;
    }

    // =========================
    // FIND RESERVATION
    // =========================

    private Reservation findReservation(String id) {

        for (Reservation r : reservations) {

            if (r.getReservationId().equalsIgnoreCase(id)) {
                return r;
            }
        }

        return null;
    }

    // =========================
    // FILE I/O - SAVE
    // =========================

    private void saveReservations() {

        try (BufferedWriter writer =
                     new BufferedWriter(
                             new FileWriter(FILE_NAME))) {

            for (Reservation r : reservations) {

                writer.write(r.toFileString());
                writer.newLine();
            }

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error saving reservations: "
                            + e.getMessage()
            );
        }
    }

    // =========================
    // FILE I/O - LOAD
    // =========================

    private void loadReservations() {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader =
                     new BufferedReader(
                             new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (!line.trim().isEmpty()) {

                    Reservation r =
                            Reservation.fromFileString(line);

                    if (r != null) {
                        reservations.add(r);
                    }
                }
            }

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading reservations: "
                            + e.getMessage()
            );
        }
    }

    // =========================
    // MAIN METHOD
    // =========================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                HotelReservationSystem::new
        );
    }
}


// ======================================================
// ROOM CLASS
// ======================================================

class Room {

    private int roomNumber;
    private String category;
    private double pricePerNight;

    public Room(
            int roomNumber,
            String category,
            double pricePerNight) {

        this.roomNumber = roomNumber;
        this.category = category;
        this.pricePerNight = pricePerNight;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getCategory() {
        return category;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }
}


// ======================================================
// RESERVATION CLASS
// ======================================================

class Reservation {

    private String reservationId;
    private String guestName;
    private String phone;
    private int roomNumber;
    private String category;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private double amount;
    private String paymentId;
    private String paymentStatus;
    private String status;

    public Reservation(
            String reservationId,
            String guestName,
            String phone,
            int roomNumber,
            String category,
            LocalDate checkIn,
            LocalDate checkOut,
            double amount,
            String paymentId,
            String paymentStatus,
            String status) {

        this.reservationId = reservationId;
        this.guestName = guestName;
        this.phone = phone;
        this.roomNumber = roomNumber;
        this.category = category;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.amount = amount;
        this.paymentId = paymentId;
        this.paymentStatus = paymentStatus;
        this.status = status;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getPhone() {
        return phone;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toFileString() {

        return reservationId + "|" +
                guestName + "|" +
                phone + "|" +
                roomNumber + "|" +
                category + "|" +
                checkIn + "|" +
                checkOut + "|" +
                amount + "|" +
                paymentId + "|" +
                paymentStatus + "|" +
                status;
    }

    public static Reservation fromFileString(String line) {

        try {

            String[] data = line.split("\\|");

            if (data.length != 11) {
                return null;
            }

            return new Reservation(
                    data[0],
                    data[1],
                    data[2],
                    Integer.parseInt(data[3]),
                    data[4],
                    LocalDate.parse(data[5]),
                    LocalDate.parse(data[6]),
                    Double.parseDouble(data[7]),
                    data[8],
                    data[9],
                    data[10]
            );

        } catch (Exception e) {

            return null;
        }
    }

    public String getDetailedInformation() {

        long nights =
                checkOut.toEpochDay()
                        - checkIn.toEpochDay();

        return
                "========================================\n" +
                "          HOTEL BOOKING DETAILS\n" +
                "========================================\n\n" +

                "Reservation ID : " + reservationId + "\n" +
                "Guest Name     : " + guestName + "\n" +
                "Phone Number   : " + phone + "\n\n" +

                "Room Number    : " + roomNumber + "\n" +
                "Room Category  : " + category + "\n" +
                "Check-in       : " + checkIn + "\n" +
                "Check-out      : " + checkOut + "\n" +
                "Number of Nights: " + nights + "\n\n" +

                "Amount         : ₹" + amount + "\n" +
                "Payment ID     : " + paymentId + "\n" +
                "Payment Status : " + paymentStatus + "\n" +
                "Booking Status : " + status + "\n\n" +

                "========================================";
    }
}