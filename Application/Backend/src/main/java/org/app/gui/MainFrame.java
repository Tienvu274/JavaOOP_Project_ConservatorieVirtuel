package org.app.gui;

import org.app.database.*;
import org.app.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainFrame extends JFrame {
    private final Backend backend = new Backend();
    private final JLabel status = new JLabel("Not connected. Configure .env, then click Connect / Initialize.");
    private final JTabbedPane tabs = new JTabbedPane();
    private final List<JButton> buttons = new ArrayList<>();
    private final DefaultTableModel students = model("ID", "Full name", "Email", "Phone", "Level");
    private final DefaultTableModel teachers = model("ID", "Full name", "Specializations", "Availability (Paris)");
    private final DefaultTableModel rooms = model("ID", "Room name", "Capacity", "Price (cents/hour)");
    private final DefaultTableModel instruments = model("ID", "Instrument name", "Type", "Price (cents/hour)");
    private final DefaultTableModel packages = model("ID", "Package name", "Price (cents)", "Lessons");
    private final DefaultTableModel enrollments = model("Enrollment ID", "Student", "Package", "Valid from", "Valid through", "Available minutes");
    private final DefaultTableModel activities = model("ID", "Type", "Start (Paris)", "End", "Status", "Details");
    private final DefaultTableModel billing = model("Type", "ID", "Student", "Amount (cents)", "Time (Paris)", "Description / Method");
    private final DefaultTableModel exams = model("ID", "Name", "Type", "Room", "Start (Paris)", "Duration", "Capacity", "Registered", "Status");
    private boolean connected;

    public MainFrame() {
        super("Virtual Conservatory - Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 700);
        setMinimumSize(new Dimension(850, 550));
        setLocationRelativeTo(null);
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JLabel title = new JLabel("VIRTUAL CONSERVATORY");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 21f));
        top.add(title, BorderLayout.WEST);
        JPanel controls = new JPanel();
        button(controls, "Connect / Initialize", () -> runTask(() -> {
            DbInitializer.initialize(); return "Database connected and initialized.";
        }, true));
        button(controls, "Refresh", () -> refresh());
        top.add(controls, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);
        JTabbedPane people = new JTabbedPane();
        people.addTab("Student", directoryPage(students, "Student", () -> saveStudent(false), () -> saveStudent(true)));
        JPanel teacherPage = directoryPage(teachers, "Teacher", () -> saveTeacher(false), () -> saveTeacher(true));
        JPanel teacherActions = (JPanel) teacherPage.getComponent(0);
        button(teacherActions, "Add specialization", this::addSpecialization);
        button(teacherActions, "Add availability", this::addAvailability);
        people.addTab("Teacher", teacherPage);
        people.addTab("Room", directoryPage(rooms, "Room", () -> saveRoom(false), () -> saveRoom(true)));
        people.addTab("Instruments", directoryPage(instruments, "Instrument", () -> saveInstrument(false), () -> saveInstrument(true)));
        tabs.addTab("Directory", people);
        JPanel p = page(packages);
        button((JPanel)p.getComponent(0), "Create package", this::createPackage);
        tabs.addTab("Packages", p);
        p = page(enrollments);
        button((JPanel)p.getComponent(0), "Enroll student", this::enroll);
        button((JPanel)p.getComponent(0), "Learning time / History", this::usage);
        tabs.addTab("Enrollments", p);
        p = page(activities); JPanel actions = (JPanel)p.getComponent(0);
        button(actions, "Schedule lesson", this::schedule);
        button(actions, "Rent room / Instrument", this::rent);
        button(actions, "Complete", () -> changeActivity(false));
        button(actions, "Cancel activity", () -> changeActivity(true));
        button(actions, "Personal schedule", this::personalSchedule);
        tabs.addTab("Schedule & Rentals", p);
        p = page(billing); actions = (JPanel)p.getComponent(0);
        button(actions, "Record payment", this::recordPayment);
        button(actions, "Student balance", this::studentBilling);
        button(actions, "Monthly summary", this::monthlyBilling);
        tabs.addTab("Charges / Payments", p);
        addExamTab();
        add(tabs, BorderLayout.CENTER);
        status.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(status, BorderLayout.SOUTH);
    }

    private static DefaultTableModel model(String... names) {
        return new DefaultTableModel(names, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
    }
    private JScrollPane table(DefaultTableModel model) {
        JTable table = new JTable(model); table.setRowHeight(28);
        table.setAutoCreateRowSorter(true); table.setFillsViewportHeight(true);
        return new JScrollPane(table);
    }
    private JPanel page(DefaultTableModel model) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JPanel(new FlowLayout(FlowLayout.LEFT)), BorderLayout.NORTH);
        p.add(table(model), BorderLayout.CENTER); return p;
    }
    private void button(JPanel panel, String name, Runnable action) {
        JButton button = new JButton(name); buttons.add(button); panel.add(button);
        button.addActionListener(e -> {
            try {
                if (!connected && !name.equals("Connect / Initialize"))
                    throw new IllegalStateException("Connect to the database first.");
                action.run();
            } catch (Exception ex) { error(ex); }
        });
    }
    private void error(Throwable ex) {
        String message = ex.getMessage();
        if (ex instanceof NumberFormatException) message = "Prices, lesson counts and minutes must be valid whole numbers.";
        if (ex instanceof java.time.format.DateTimeParseException) message = "Date: yyyy-MM-dd. Date/time: yyyy-MM-dd HH:mm. Month: yyyy-MM.";
        JOptionPane.showMessageDialog(this, message, "Operation failed", JOptionPane.ERROR_MESSAGE);
    }
    @FunctionalInterface private interface Task { Object execute() throws Exception; }
    private void runTask(Task task, boolean reload) {
        buttons.forEach(b -> b.setEnabled(false)); status.setText("Processing...");
        new SwingWorker<Object, Void>() {
            protected Object doInBackground() throws Exception { return task.execute(); }
            protected void done() {
                buttons.forEach(b -> b.setEnabled(true));
                try {
                    Object result = get();
                    status.setText("Done.");
                    if (result != null) showResult(result.toString());
                    if (reload) { connected = true; refresh(); }
                } catch (Exception ex) {
                    status.setText("Operation failed. Check your input or MySQL settings in .env.");
                    error(ex.getCause() == null ? ex : ex.getCause());
                }
            }
        }.execute();
    }
    private void showResult(String value) {
        JTextArea area = new JTextArea(value, 14, 75); area.setEditable(false);
        area.setLineWrap(true); area.setWrapStyleWord(true); area.setCaretPosition(0);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Result", JOptionPane.INFORMATION_MESSAGE);
    }
    private void refresh() {
        runTask(() -> {
            List<Object[][]> rows = new ArrayList<>();
            rows.add(DbLoader.loadStudents().stream().map(s -> new Object[]{s.getId(),s.getFullName(),s.getEmail(),s.getPhone(),s.getLevel()}).toArray(Object[][]::new));
            rows.add(DbLoader.loadTeachers().stream().map(t -> new Object[]{t.getId(),t.getFullName(),t.getSpecializations(),t.getAvailability()}).toArray(Object[][]::new));
            rows.add(DbLoader.loadRooms().stream().map(r -> new Object[]{r.getId(),r.getName(),r.getCapacity(),r.getHourlyPriceCents()}).toArray(Object[][]::new));
            rows.add(DbLoader.loadInstruments().stream().map(i -> new Object[]{i.getId(),i.getName(),i.getType(),i.getHourlyPriceCents()}).toArray(Object[][]::new));
            rows.add(backend.packages.getAllPackages().stream().map(p -> new Object[]{p.getId(),p.getName(),p.getPriceCents(),p.isUnlimited()?"Unlimited":p.getLessonLimit()}).toArray(Object[][]::new));
            List<LessonParticipation> history = DbLoader.loadParticipations();
            rows.add(DbLoader.loadEnrollments().stream().map(e -> new Object[]{e.getId(),e.getStudentId(),e.getCoursePackage().getName(),e.getValidFrom(),e.getValidUntil(),minutes(e.availableMinutes(LocalDate.now(DbTime.ZONE),history))}).toArray(Object[][]::new));
            rows.add(DbLoader.loadActivities().stream().map(a -> new Object[]{a.getId(),a.getKind(),a.getStart(),a.getEnd(),a.getStatus(),details(a)}).toArray(Object[][]::new));
            List<Object[]> money = new ArrayList<>();
            for (Charge c : DbLoader.loadCharges()) money.add(new Object[]{"Charge",c.getId(),c.getStudentId(),c.getAmountCents(),c.getCreatedAt(),c.getDescription()});
            for (Payment p : DbLoader.loadPayments()) money.add(new Object[]{"Payment",p.getId(),p.getStudentId(),p.getAmountCents(),p.getPaidAt(),p.getMethod()});
            rows.add(money.toArray(new Object[0][]));

            rows.add(backend.exams.getAllExams().stream().map(e -> new Object[]{
                    e.getId(), e.getName(), e.getInstrumentType(), e.getRoomId(), e.getStart(),
                    e.getDurationMinutes(), e.getCapacity(), e.getRegistrations().size(), e.getStatus()
            }).toArray(Object[][]::new));

            SwingUtilities.invokeLater(() -> {
                DefaultTableModel[] models = {students,teachers,rooms,instruments,packages,enrollments,activities,billing,exams};
                for (int i=0;i<models.length;i++) { models[i].setRowCount(0); for(Object[] row:rows.get(i)) models[i].addRow(row); }
            });
            return null;
        }, false);
    }
    private static String minutes(Integer n) { return n == null ? "Unlimited" : n.toString(); }
    private void recordPayment() {
        InputForm f = new InputForm("Student ID", "Payment amount (cents)");
        JComboBox<String> method = new JComboBox<>(new String[]{"CASH", "CARD", "TRANSFER"});
        f.add(new JLabel("Method")); f.add(method);
        if (!f.show(this, "Record payment; check the balance before entering an amount")) return;
        String student = f.text(0);
        long amount = f.money(1);
        if (amount <= 0) throw new IllegalArgumentException("The payment amount must be greater than 0.");
        String selected = (String)method.getSelectedItem();
        if (JOptionPane.showConfirmDialog(this, "Record " + amount + " cents for " + student
                + " using " + selected + "?", "Confirm payment", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        runTask(() -> {
            Payment payment = backend.billing.recordPayment(student, amount, selected);
            return "Payment saved: " + payment.getId() + "\nStudent: " + payment.getStudentId()
                    + "\nAmount: " + payment.getAmountCents() + " cents\nMethod: " + payment.getMethod()
                    + "\nTime (Paris): " + payment.getPaidAt();
        }, true);
    }
    private void studentBilling() {
        String input = JOptionPane.showInputDialog(this, "Student ID:");
        if (input == null) return;
        String student = input.trim();
        runTask(() -> {
            List<Charge> charges = backend.billing.getStudentCharges(student);
            List<Payment> payments = backend.billing.getStudentPayments(student);
            long charged = backend.billing.getTotalChargedCents(student);
            long paid = backend.billing.getTotalPaidCents(student);
            long balance = backend.billing.getBalanceCents(student);
            StringBuilder text = new StringBuilder("Student: " + student
                    + "\nTotal charged: " + charged + " cents\nTotal paid: " + paid
                    + " cents\nOutstanding balance: " + balance + " cents\n\nCHARGES:\n");
            if (charges.isEmpty()) text.append("No charges found.\n");
            for (Charge c : charges) text.append(c.getId()).append(" | ").append(c.getAmountCents())
                    .append(" cents | ").append(c.getCreatedAt()).append(" | ").append(c.getDescription()).append('\n');
            text.append("\nPAYMENT HISTORY:\n");
            if (payments.isEmpty()) text.append("No payments found.\n");
            for (Payment p : payments) text.append(p.getId()).append(" | ").append(p.getAmountCents())
                    .append(" cents | ").append(p.getPaidAt()).append(" | ").append(p.getMethod()).append('\n');
            return text;
        }, false);
    }
    private void monthlyBilling() {
        String input = JOptionPane.showInputDialog(this, "Month (yyyy-MM), Paris time:",
                YearMonth.now(DbTime.ZONE).toString());
        if (input == null) return;
        YearMonth month = YearMonth.parse(input.trim());
        runTask(() -> "Monthly summary " + month + " (Paris)\nTotal charges created: "
                + backend.billing.getMonthlyChargedCents(month) + " cents\nTotal payments received: "
                + backend.billing.getMonthlyPaidCents(month) + " cents", false);
    }
    private static String details(Activity activity) {
        if (activity instanceof Lesson lesson)
            return "Teacher: " + lesson.getTeacherId() + " | Room: " + lesson.getRoomId()
                    + " | " + lesson.getInstrumentType() + " | Student: "
                    + lesson.getParticipants().stream().map(LessonParticipation::getStudentId).toList();
        if (activity instanceof Rental rental)
            return "Student: " + rental.getStudentId() + " | " + rental.getResourceType() + ": " + rental.getResourceId();
        return activity.toString();
    }

    private void createPackage() {
        InputForm f = new InputForm("Package ID", "Package name", "Price (cents; 100 cents = 1 EUR)", "Lessons (0 = unlimited)");
        if (!f.show(this,"Create package")) return;
        int count = f.number(3);
        CoursePackage p = count == 0 ? new UnlimitedPackage(f.text(0),f.text(1),f.money(2))
                : new FinitePackage(f.text(0),f.text(1),f.money(2),count);
        runTask(() -> backend.packages.createPackage(p), true);
    }
    private void enroll() {
        InputForm f = new InputForm("New enrollment ID", "New charge ID", "Student ID", "Package ID", "Valid from (yyyy-MM-dd)", "Valid through (yyyy-MM-dd)");
        f.preset(4,LocalDate.now(DbTime.ZONE).toString()); f.preset(5,LocalDate.now(DbTime.ZONE).plusMonths(1).toString());
        if (!f.show(this,"Enroll in a package")) return;
        String id=f.text(0), charge=f.text(1), student=f.text(2), pack=f.text(3);
        LocalDate from=LocalDate.parse(f.text(4)), until=LocalDate.parse(f.text(5));
        runTask(() -> backend.enrollments.enrollStudent(id,charge,student,pack,from,until),true);
    }
    private void usage() {
        String id=JOptionPane.showInputDialog(this,"Student ID:"); if(id==null)return;
        runTask(() -> {
            StringBuilder text=new StringBuilder("Learning time as of today (Paris)\n");
            for(EnrollmentSummary s:backend.enrollments.getStudentPackageSummaries(id.trim(),LocalDate.now(DbTime.ZONE)))
                text.append(s.getEnrollment().getId()).append(" | ").append(s.getValidityStatus())
                        .append(" | Available: ").append(minutes(s.getAvailableMinutes()))
                        .append(" minutes | Used: ").append(s.getConsumedMinutes()).append(" | Reserved: ").append(s.getReservedMinutes()).append('\n');
            text.append("\nHistory:\n");
            for(LessonParticipation p:backend.enrollments.getUsageHistory(id.trim()))text.append(p).append('\n');
            return text;
        },false);
    }
    private void schedule() {
        InputForm f=new InputForm("New lesson ID", "Start (yyyy-MM-dd HH:mm, Paris)", "Teacher ID", "Room ID", "Type (PIANO/GUITAR/VIOLIN/FLUTE/DRUMS)", "Instrument IDs (comma-separated)", "Student IDs (comma-separated)", "Matching enrollment IDs (blank = pay per lesson)");
        if(!f.show(this,"Schedule a 60-minute lesson; 2 or more students form a group"))return;
        String id=f.text(0); List<String> ids=split(f.text(6));
        String[] enrollmentIds=f.text(7).split(",",-1);
        if(!f.text(7).isEmpty() && enrollmentIds.length!=ids.size())throw new IllegalArgumentException("Enrollment IDs must match the number of students; use commas to preserve empty positions.");
        List<LessonParticipation> participants=new ArrayList<>();
        for(int i=0;i<ids.size();i++) {
            String en=f.text(7).isEmpty()?"":enrollmentIds[i].trim();
            participants.add(new LessonParticipation(id,ids.get(i),en.isEmpty()?null:en,60,LessonParticipation.State.RESERVED,LocalDateTime.now(DbTime.ZONE).withNano(0),null));
        }
        Lesson lesson=new Lesson(id,f.time(1),f.text(2),f.text(3),f.text(4).toUpperCase(java.util.Locale.ROOT),ids.size()>1,split(f.text(5)),Activity.Status.SCHEDULED,null,participants);
        runTask(() -> backend.scheduling.scheduleLesson(lesson),true);
    }
    private List<String> split(String value) { return Arrays.stream(value.split(",",-1)).map(String::trim).toList(); }
    private void rent() {
        JComboBox<Rental.ResourceType> types=new JComboBox<>(Rental.ResourceType.values());
        if(JOptionPane.showConfirmDialog(this,types,"Choose ROOM or INSTRUMENT",JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION)return;
        Rental.ResourceType type=(Rental.ResourceType)types.getSelectedItem();
        InputForm f=new InputForm("New rental ID","Student ID","Room / Instrument ID","Start (yyyy-MM-dd HH:mm, Paris)","Duration (minutes)"); f.preset(4,"60");
        if(!f.show(this,"Create rental: "+type))return;
        String id=f.text(0), student=f.text(1), resource=f.text(2); LocalDateTime time=f.time(3); int duration=f.number(4);
        runTask(() -> backend.rentals.createRental(id,student,type,resource,time,duration),true);
    }
    private void changeActivity(boolean cancel) {
        JPanel panel=(JPanel)tabs.getComponentAt(3);
        JTable table=(JTable)((JScrollPane)panel.getComponent(1)).getViewport().getView();
        int selected=table.getSelectedRow();
        if(selected<0)throw new IllegalArgumentException("Select an activity in the table first.");
        int row=table.convertRowIndexToModel(selected);
        String id=activities.getValueAt(row,0).toString(), kind=activities.getValueAt(row,1).toString();
        if(kind.equals("EXAM") && cancel)throw new IllegalArgumentException("Exam cancellation is not available. Use Complete exam for completed exams.");
        runTask(() -> {
            if (kind.equals("EXAM")) {
                backend.exams.completeExam(id, nowParis());
                return "Exam completed.";
            }
            if (kind.equals("LESSON")) return cancel ? backend.scheduling.cancelLesson(id) : backend.scheduling.completeLesson(id);
            return cancel ? backend.rentals.cancelRental(id) : backend.rentals.completeRental(id);
        }, true);
    }
    private void personalSchedule() {
        String[] choices={"Student","Teacher"};
        int choice=JOptionPane.showOptionDialog(this,"Whose schedule would you like to view?","Personal schedule",JOptionPane.DEFAULT_OPTION,JOptionPane.QUESTION_MESSAGE,null,choices,choices[0]);
        if(choice<0)return;
        String id=JOptionPane.showInputDialog(this,"Enter ID for "+choices[choice]+":"); if(id==null)return;
        runTask(() -> {
            List<Activity> list=choice==0?backend.scheduling.getStudentActivities(id.trim()):backend.scheduling.getTeacherActivities(id.trim());
            StringBuilder text=new StringBuilder(); for(Activity a:list)text.append(a).append('\n');
            return text.length()==0?"No activities found.":text.toString();
        },false);
    }

    private JPanel directoryPage(DefaultTableModel data, String kind, Runnable create, Runnable update) {
        JPanel panel = page(data);
        JPanel actions = (JPanel) panel.getComponent(0);
        button(actions, "List / Refresh", this::refresh);
        button(actions, "Find by ID", () -> findRecord(kind));
        button(actions, "Create", create);
        button(actions, "Update", update);
        return panel;
    }

    private void findRecord(String kind) {
        String value = JOptionPane.showInputDialog(this, kind + " ID:");
        if (value == null) return;
        String id = value.trim();

        runTask(() -> {
            if (kind.equals("Student")) {
                Student s = backend.students.getStudentById(id);
                return s + "\nAddress: " + s.getAddress() + "\nBirth date: " + s.getBirthDate()
                        + "\nPhone: " + s.getPhone() + "\nEmail: " + s.getEmail()
                        + "\nPreferences: " + s.getPreferredInstruments();
            }

            if (kind.equals("Teacher")) {
                Teacher t = backend.teachers.getTeacherById(id);
                return t + "\nRate: " + t.getHourlyRateCents() + " cents/hour"
                        + "\nQualifications: " + t.getQualifications() + "\nAvailability: " + t.getAvailability();
            }

            if (kind.equals("Room")) {
                Room r = backend.resources.getRoomById(id);
                return r + "\nPrice: " + r.getHourlyPriceCents() + " cents/hour";
            }

            if (kind.equals("Instrument")) {
                Instrument i = backend.resources.getInstrumentById(id);
                return i + "\nPrice: " + i.getHourlyPriceCents() + " cents/hour";
            }

            Exam e = backend.exams.getExamById(id);
            return e + "\nName: " + e.getName() + "\nType: " + e.getInstrumentType()
                    + "\nRoom: " + e.getRoomId() + "\nCapacity: " + e.getCapacity()
                    + "\nInstruments: " + e.getInstrumentIds() + "\nRegistrations:\n" + lines(e.getRegistrations());
        }, false);
    }

    private List<String> optionalList(String value) {
        List<String> result = new ArrayList<>();
        if (value.isBlank()) return result;

        for (String part : value.split(",", -1)) {
            String item = part.trim();
            if (item.isEmpty()) throw new IllegalArgumentException("Do not leave an empty item between commas.");
            result.add(item);
        }
        return result;
    }

    private String lines(List<?> values) {
        if (values.isEmpty()) return "No records.";
        StringBuilder text = new StringBuilder();
        for (Object value : values) text.append(value).append('\n');
        return text.toString();
    }

    private LocalDateTime nowParis() {
        return LocalDateTime.now(DbTime.ZONE).withNano(0);
    }

    private void saveStudent(boolean update) {
        InputForm f = new InputForm(
                "Student ID", "First name", "Last name", "Address", "Birth date (yyyy-MM-dd)",
                "Phone", "Email", "Level (BEGINNER/INTERMEDIATE/ADVANCED)",
                "Preferred types (comma-separated; blank = none)"
        );

        if (!f.show(this, update ? "Update student - re-enter all fields" : "Create student")) return;

        Student student = new Student(
                f.text(0), f.text(1), f.text(2), f.text(3), LocalDate.parse(f.text(4)),
                f.text(5), f.text(6), f.text(7), optionalList(f.text(8))
        );

        runTask(() -> {
            if (update) backend.students.updateStudent(student);
            else backend.students.createStudent(student);
            return "Student saved: " + student.getId();
        }, true);
    }

    private void saveTeacher(boolean update) {
        InputForm f = new InputForm("Teacher ID", "First name", "Last name", "Hourly rate (cents)", "Qualifications");
        if (!f.show(this, update ? "Update teacher - re-enter all basic fields" : "Create teacher")) return;

        Teacher teacher = new Teacher(f.text(0), f.text(1), f.text(2), f.money(3), f.text(4));

        runTask(() -> {
            if (update) backend.teachers.updateTeacher(teacher);
            else backend.teachers.createTeacher(teacher);
            return "Teacher saved: " + teacher.getId()
                    + "\nUse Add specialization and Add availability to configure teaching.";
        }, true);
    }

    private void addSpecialization() {
        InputForm f = new InputForm("Teacher ID", "Type (PIANO/GUITAR/VIOLIN/FLUTE/DRUMS)");
        if (!f.show(this, "Add specialization")) return;

        String id = f.text(0), type = f.text(1);

        runTask(() -> {
            backend.teachers.addSpecialization(id, type);
            return "Specialization added.";
        }, true);
    }

    private void addAvailability() {
        InputForm f = new InputForm("Teacher ID", "Start (yyyy-MM-dd HH:mm, Paris)", "End (yyyy-MM-dd HH:mm, Paris)");
        if (!f.show(this, "Add availability")) return;

        String id = f.text(0);
        LocalDateTime start = f.time(1), end = f.time(2);

        runTask(() -> "Availability added. Slot number: "
                + backend.teachers.addAvailability(id, start, end), true);
    }

    private void saveRoom(boolean update) {
        InputForm f = new InputForm("Room ID", "Room name", "Capacity", "Hourly price (cents)");
        if (!f.show(this, update ? "Update room - re-enter all fields" : "Create room")) return;

        Room room = new Room(f.text(0), f.text(1), f.number(2), f.money(3));

        runTask(() -> {
            if (update) backend.resources.updateRoom(room);
            else backend.resources.createRoom(room);
            return "Room saved: " + room.getId();
        }, true);
    }

    private void saveInstrument(boolean update) {
        InputForm f = new InputForm(
                "Instrument ID", "Instrument name",
                "Type (PIANO/GUITAR/VIOLIN/FLUTE/DRUMS)", "Hourly price (cents)"
        );
        if (!f.show(this, update ? "Update instrument - re-enter all fields" : "Create instrument")) return;

        Instrument instrument = new Instrument(f.text(0), f.text(1), f.text(2), f.money(3));

        runTask(() -> {
            if (update) backend.resources.updateInstrument(instrument);
            else backend.resources.createInstrument(instrument);
            return "Instrument saved: " + instrument.getId();
        }, true);
    }

    private void addExamTab() {
        JPanel panel = page(exams);
        JPanel actions = (JPanel) panel.getComponent(0);
        actions.setLayout(new GridLayout(2, 4, 5, 5));

        button(actions, "List / Refresh", this::refresh);
        button(actions, "Find by ID", () -> findRecord("Exam"));
        button(actions, "Create exam", () -> createExam(false));
        button(actions, "Check conflicts", () -> createExam(true));
        button(actions, "Register student", this::registerExam);
        button(actions, "Registrations / Results", this::examRegistrations);
        button(actions, "Complete exam", this::completeExam);
        button(actions, "Record result", this::recordExamResult);

        tabs.addTab("Exams", panel);
    }

    private void createExam(boolean checkOnly) {
        InputForm f = new InputForm(
                "New exam ID", "Exam name", "Type (PIANO/GUITAR/VIOLIN/FLUTE/DRUMS)", "Room ID",
                "Start (yyyy-MM-dd HH:mm, Paris)", "Duration (minutes)", "Capacity",
                "Instrument IDs (comma-separated; optional)"
        );
        if (!f.show(this, checkOnly ? "Check proposed exam - nothing is saved" : "Create exam")) return;

        Exam exam = new Exam(
                f.text(0), f.text(1), f.text(2), f.text(3), f.time(4), f.number(5), f.number(6),
                optionalList(f.text(7)), Activity.Status.SCHEDULED, null, new ArrayList<>()
        );

        runTask(() -> {
            if (checkOnly) {
                List<String> conflicts = backend.exams.findConflicts(exam);
                return conflicts.isEmpty() ? "No room or instrument time conflicts found." : lines(conflicts);
            }

            backend.exams.createExam(exam);
            return "Exam created: " + exam.getId();
        }, !checkOnly);
    }

    private void registerExam() {
        InputForm f = new InputForm("Exam ID", "Student ID");
        if (!f.show(this, "Register student for exam")) return;

        String exam = f.text(0), student = f.text(1);

        runTask(() -> {
            backend.exams.registerStudent(exam, student, nowParis());
            return "Student registered.";
        }, true);
    }

    private void examRegistrations() {
        String id = JOptionPane.showInputDialog(this, "Exam ID:");
        if (id == null) return;

        runTask(() -> lines(backend.exams.getRegistrations(id.trim())), false);
    }

    private void completeExam() {
        String id = JOptionPane.showInputDialog(this, "Exam ID:");
        if (id == null) return;

        if (JOptionPane.showConfirmDialog(this, "Complete " + id.trim() + "?",
                "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        runTask(() -> {
            backend.exams.completeExam(id.trim(), nowParis());
            return "Exam completed.";
        }, true);
    }

    private void recordExamResult() {
        InputForm f = new InputForm("Exam ID", "Student ID", "Score (optional; blank = no score)");
        JComboBox<String> outcome = new JComboBox<>(new String[]{"PASS", "FAIL"});
        f.add(new JLabel("Result"));
        f.add(outcome);

        if (!f.show(this, "Record exam result")) return;

        String exam = f.text(0), student = f.text(1);
        java.math.BigDecimal score = f.text(2).isEmpty() ? null : new java.math.BigDecimal(f.text(2));
        boolean passed = "PASS".equals(outcome.getSelectedItem());

        runTask(() -> {
            backend.exams.recordResult(exam, student, passed, score, nowParis());
            return "Result recorded.";
        }, true);
    }
}

