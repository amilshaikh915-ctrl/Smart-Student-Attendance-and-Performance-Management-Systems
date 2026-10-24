package com.smartcampus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            new DashboardFrame().setVisible(true);
        });
    }
}

class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    String id, name, department, email;
    int semester;
    Student(String id, String name, String department, String email, int semester) {
        this.id=id; this.name=name; this.department=department; this.email=email; this.semester=semester;
    }
}
class AttendanceRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    String studentId, date, status;
    AttendanceRecord(String studentId, String date, String status) { this.studentId=studentId; this.date=date; this.status=status; }
}
class MarkRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    String studentId, subject, exam;
    double score, max;
    MarkRecord(String studentId, String subject, String exam, double score, double max) { this.studentId=studentId; this.subject=subject; this.exam=exam; this.score=score; this.max=max; }
}
class CampusStore implements Serializable {
    private static final long serialVersionUID = 1L;
    List<Student> students = new ArrayList<>();
    List<AttendanceRecord> attendance = new ArrayList<>();
    List<MarkRecord> marks = new ArrayList<>();
    private static final Path FILE = Paths.get("data", "campus-data.ser");
    static CampusStore load() {
        try {
            if (Files.exists(FILE)) try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(FILE))) { return (CampusStore) in.readObject(); }
        } catch (Exception ex) { JOptionPane.showMessageDialog(null, "Could not read saved data. A new data store will be used.\n"+ex.getMessage(), "Storage notice", JOptionPane.WARNING_MESSAGE); }
        CampusStore s = new CampusStore(); s.seed(); s.save(); return s;
    }
    void save() {
        try { Files.createDirectories(FILE.getParent()); try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(FILE))) { out.writeObject(this); } }
        catch (IOException ex) { JOptionPane.showMessageDialog(null, "Could not save data: " + ex.getMessage(), "Storage error", JOptionPane.ERROR_MESSAGE); }
    }
    void seed() {
        students.add(new Student("CE001", "Aarav Mehta", "Computer Engineering", "aarav@example.com", 3));
        students.add(new Student("CE002", "Sara Khan", "Computer Engineering", "sara@example.com", 3));
        students.add(new Student("CE003", "Rohan Patil", "Information Technology", "rohan@example.com", 3));
        students.add(new Student("CE004", "Anaya Shah", "AI & Machine Learning", "anaya@example.com", 3));
        students.add(new Student("CE005", "Kabir Joshi", "Computer Engineering", "kabir@example.com", 3));
        String today=LocalDate.now().toString();
        for (int i=0;i<students.size();i++) {
            attendance.add(new AttendanceRecord(students.get(i).id, today, i==2?"Absent":"Present"));
            marks.add(new MarkRecord(students.get(i).id,"Programming","Internal Test",new double[]{84,76,52,91,67}[i],100));
            marks.add(new MarkRecord(students.get(i).id,"Mathematics","Internal Test",new double[]{78,88,49,82,73}[i],100));
        }
    }
    Student byId(String id) { for (Student s:students) if (s.id.equals(id)) return s; return null; }
    long presentCount(String id) { return attendance.stream().filter(a->a.studentId.equals(id)&&a.status.equals("Present")).count(); }
    long attendanceCount(String id) { return attendance.stream().filter(a->a.studentId.equals(id)).count(); }
    double attendancePct(String id) { long n=attendanceCount(id); return n==0?0:100.0*presentCount(id)/n; }
    double average(String id) { return marks.stream().filter(m->m.studentId.equals(id)).mapToDouble(m->m.max<=0?0:m.score/m.max*100).average().orElse(0); }
}

class DashboardFrame extends JFrame {
    private final CampusStore store = CampusStore.load();
    private final Color navy = new Color(22, 35, 61), blue = new Color(55, 105, 220), bg = new Color(244,247,251), ink = new Color(37,48,67), muted = new Color(111,124,145);
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pages = new JPanel(cardLayout);
    private JTable studentTable, attendanceTable, marksTable, reportTable;
    private DefaultTableModel studentModel, attendanceModel, marksModel, reportModel;
    private JLabel totalValue, attendanceValue, averageValue, riskValue;
    private String activePage="Overview";
    DashboardFrame() {
        setTitle("Smart Student Attendance & Performance Management System"); setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(1180,760); setMinimumSize(new Dimension(980,650)); setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout()); getContentPane().setBackground(bg);
        JPanel sidebar=new JPanel(); sidebar.setBackground(navy); sidebar.setPreferredSize(new Dimension(225,0)); sidebar.setLayout(new BoxLayout(sidebar,BoxLayout.Y_AXIS)); sidebar.setBorder(new EmptyBorder(24,14,18,14));
        JLabel brand=new JLabel("  SMARTCAMPUS"); brand.setForeground(Color.WHITE); brand.setFont(new Font("SansSerif",Font.BOLD,19)); brand.setAlignmentX(LEFT_ALIGNMENT); sidebar.add(brand);
        JLabel sub=new JLabel("  STUDENT SUCCESS SUITE"); sub.setForeground(new Color(166,185,220)); sub.setFont(new Font("SansSerif",Font.PLAIN,10)); sub.setBorder(new EmptyBorder(6,0,28,0)); sub.setAlignmentX(LEFT_ALIGNMENT); sidebar.add(sub);
        String[] nav={"Overview","Students","Attendance","Performance","Reports"};
        for(String item:nav){ JButton b=new JButton("   "+item); b.setHorizontalAlignment(SwingConstants.LEFT); b.setMaximumSize(new Dimension(200,44)); b.setPreferredSize(new Dimension(195,44)); b.setFocusPainted(false); b.setBorderPainted(false); b.setOpaque(true); b.setBackground(item.equals("Overview")?new Color(48,67,104):navy); b.setForeground(Color.WHITE); b.setFont(new Font("SansSerif",Font.PLAIN,14)); b.setAlignmentX(LEFT_ALIGNMENT); b.addActionListener(e->showPage(item)); sidebar.add(b); sidebar.add(Box.createVerticalStrut(5)); }
        sidebar.add(Box.createVerticalGlue()); JLabel user=new JLabel("  SHAHREYAR AKHTER"); user.setForeground(Color.WHITE); user.setFont(new Font("SansSerif",Font.BOLD,11)); sidebar.add(user); JLabel role=new JLabel("  Roll 251822  •  Div B"); role.setForeground(new Color(166,185,220)); role.setFont(new Font("SansSerif",Font.PLAIN,11)); role.setBorder(new EmptyBorder(5,0,0,0)); sidebar.add(role);
        getContentPane().add(sidebar,BorderLayout.WEST);
        JPanel right=new JPanel(new BorderLayout()); right.setBackground(bg); JPanel top=new JPanel(new BorderLayout()); top.setBackground(Color.WHITE); top.setBorder(new EmptyBorder(17,26,17,26)); JLabel title=new JLabel("Smart Student Attendance & Performance Management System"); title.setFont(new Font("SansSerif",Font.BOLD,18)); title.setForeground(ink); top.add(title,BorderLayout.WEST); JLabel date=new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))); date.setForeground(muted); top.add(date,BorderLayout.EAST); right.add(top,BorderLayout.NORTH);
        pages.setBackground(bg); pages.add(buildOverview(),"Overview"); pages.add(buildStudents(),"Students"); pages.add(buildAttendance(),"Attendance"); pages.add(buildPerformance(),"Performance"); pages.add(buildReports(),"Reports"); right.add(pages,BorderLayout.CENTER); getContentPane().add(right,BorderLayout.CENTER); refreshAll();
    }
    void showPage(String name){activePage=name; cardLayout.show(pages,name); refreshAll();}
    JPanel pageBase(String heading,String description){ JPanel p=new JPanel(new BorderLayout(0,18)); p.setBackground(bg); p.setBorder(new EmptyBorder(24,26,26,26)); JPanel h=new JPanel(); h.setOpaque(false); h.setLayout(new BoxLayout(h,BoxLayout.Y_AXIS)); JLabel t=new JLabel(heading); t.setFont(new Font("SansSerif",Font.BOLD,23)); t.setForeground(ink); JLabel d=new JLabel(description); d.setFont(new Font("SansSerif",Font.PLAIN,13)); d.setForeground(muted); d.setBorder(new EmptyBorder(6,0,0,0)); h.add(t); h.add(d); p.add(h,BorderLayout.NORTH); return p; }
    JPanel buildOverview(){ JPanel p=pageBase("Overview","A live snapshot of attendance and academic progress."); JPanel body=new JPanel(new BorderLayout(0,18)); body.setOpaque(false); JPanel stats=new JPanel(new GridLayout(1,4,14,0)); stats.setOpaque(false); totalValue=stat(stats,"TOTAL STUDENTS","0","Registered learners",blue); attendanceValue=stat(stats,"AVG. ATTENDANCE","0%","Across recorded sessions",new Color(24,157,118)); averageValue=stat(stats,"AVG. PERFORMANCE","0%","Marks normalized to 100",new Color(136,91,208)); riskValue=stat(stats,"NEEDS ATTENTION","0","Attendance below 75%",new Color(220,137,52)); body.add(stats,BorderLayout.NORTH);
        JPanel lower=new JPanel(new GridLayout(1,2,16,0)); lower.setOpaque(false); lower.add(panelWithTitle("Attendance by student",new AttendanceChart())); lower.add(panelWithTitle("Performance by student",new PerformanceChart())); body.add(lower,BorderLayout.CENTER); p.add(body,BorderLayout.CENTER); return p; }
    JLabel stat(JPanel parent,String label,String value,String foot,Color accent){ JPanel c=new JPanel(new BorderLayout(0,10)); c.setBackground(Color.WHITE); c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(229,234,242)),new EmptyBorder(17,17,17,17))); JPanel head=new JPanel(new BorderLayout()); head.setOpaque(false); JLabel l=new JLabel(label); l.setFont(new Font("SansSerif",Font.BOLD,10)); l.setForeground(muted); head.add(l,BorderLayout.WEST); JLabel dot=new JLabel("●"); dot.setForeground(accent); head.add(dot,BorderLayout.EAST); c.add(head,BorderLayout.NORTH); JLabel v=new JLabel(value); v.setFont(new Font("SansSerif",Font.BOLD,27)); v.setForeground(ink); c.add(v,BorderLayout.CENTER); JLabel f=new JLabel(foot); f.setFont(new Font("SansSerif",Font.PLAIN,11)); f.setForeground(muted); c.add(f,BorderLayout.SOUTH); parent.add(c); return v; }
    JPanel panelWithTitle(String title,JComponent content){ JPanel p=new JPanel(new BorderLayout(0,10)); p.setBackground(Color.WHITE); p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(229,234,242)),new EmptyBorder(17,17,17,17))); JLabel l=new JLabel(title); l.setFont(new Font("SansSerif",Font.BOLD,15)); l.setForeground(ink); p.add(l,BorderLayout.NORTH); p.add(content,BorderLayout.CENTER); return p; }
    JPanel buildStudents(){ JPanel p=pageBase("Students","Maintain your student directory and academic profiles."); JPanel body=new JPanel(new BorderLayout(0,12)); body.setOpaque(false); JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0)); actions.setOpaque(false); JButton add=button("+ Add student",blue); add.addActionListener(e->studentDialog(null)); JButton edit=button("Edit selected",navy); edit.addActionListener(e->{int r=studentTable.getSelectedRow();if(r>=0)studentDialog(store.byId(String.valueOf(studentModel.getValueAt(r,0))));else info("Select a student first.");}); JButton del=button("Delete selected",new Color(185,65,75)); del.addActionListener(e->deleteStudent()); actions.add(add);actions.add(edit);actions.add(del); body.add(actions,BorderLayout.NORTH); studentModel=model("ID","Student name","Department","Email","Semester","Attendance","Average marks"); studentTable=table(studentModel); body.add(scroll(studentTable),BorderLayout.CENTER); p.add(body,BorderLayout.CENTER); return p; }
    JPanel buildAttendance(){ JPanel p=pageBase("Attendance","Record daily attendance and monitor the 75% threshold."); JPanel body=new JPanel(new BorderLayout(0,12));body.setOpaque(false); JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));actions.setOpaque(false); JButton mark=button("Mark attendance",blue);mark.addActionListener(e->attendanceDialog()); JButton today=button("Mark all present today",navy);today.addActionListener(e->markAllPresent()); JButton export=button("Export CSV",new Color(24,139,106));export.addActionListener(e->exportAttendance()); actions.add(mark);actions.add(today);actions.add(export);body.add(actions,BorderLayout.NORTH);attendanceModel=model("Date","Student ID","Student name","Status");attendanceTable=table(attendanceModel);body.add(scroll(attendanceTable),BorderLayout.CENTER);p.add(body,BorderLayout.CENTER);return p; }
    JPanel buildPerformance(){ JPanel p=pageBase("Performance","Record assessment scores and review academic progress."); JPanel body=new JPanel(new BorderLayout(0,12));body.setOpaque(false); JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));actions.setOpaque(false); JButton add=button("+ Add marks",blue);add.addActionListener(e->marksDialog()); JButton export=button("Export CSV",new Color(24,139,106));export.addActionListener(e->exportMarks());actions.add(add);actions.add(export);body.add(actions,BorderLayout.NORTH);marksModel=model("Student ID","Student name","Subject","Assessment","Score","Maximum","Percentage","Grade");marksTable=table(marksModel);body.add(scroll(marksTable),BorderLayout.CENTER);p.add(body,BorderLayout.CENTER);return p; }
    JPanel buildReports(){ JPanel p=pageBase("Reports & insights","Identify students who may need academic support."); JPanel body=new JPanel(new BorderLayout(0,12));body.setOpaque(false); JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));actions.setOpaque(false); JButton export=button("Export progress report (CSV)",blue);export.addActionListener(e->exportReport()); JButton summary=button("View summary",navy);summary.addActionListener(e->showSummary());actions.add(export);actions.add(summary);body.add(actions,BorderLayout.NORTH);reportModel=model("Student ID","Student name","Sessions","Present","Attendance %","Average marks %","Status");reportTable=table(reportModel);body.add(scroll(reportTable),BorderLayout.CENTER);p.add(body,BorderLayout.CENTER);return p; }
    DefaultTableModel model(String... cols){return new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};}
    JTable table(DefaultTableModel m){JTable t=new JTable(m);t.setRowHeight(30);t.setFont(new Font("SansSerif",Font.PLAIN,12));t.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,12));t.getTableHeader().setBackground(new Color(235,240,248));t.getTableHeader().setForeground(ink);t.setSelectionBackground(new Color(218,230,255));t.setGridColor(new Color(235,239,245));t.setFillsViewportHeight(true);return t;}
    JScrollPane scroll(JTable t){JScrollPane s=new JScrollPane(t);s.setBorder(BorderFactory.createLineBorder(new Color(229,234,242)));s.getViewport().setBackground(Color.WHITE);return s;}
    JButton button(String text,Color color){JButton b=new JButton(text);b.setFocusPainted(false);b.setBackground(color);b.setForeground(Color.WHITE);b.setFont(new Font("SansSerif",Font.BOLD,12));b.setBorder(new EmptyBorder(10,15,10,15));return b;}
    void refreshAll(){if(totalValue==null)return; totalValue.setText(String.valueOf(store.students.size())); double att=store.students.stream().mapToDouble(s->store.attendancePct(s.id)).average().orElse(0);double avg=store.students.stream().mapToDouble(s->store.average(s.id)).average().orElse(0); long risk=store.students.stream().filter(s->store.attendanceCount(s.id)>0&&store.attendancePct(s.id)<75).count();attendanceValue.setText(String.format(Locale.US,"%.1f%%",att));averageValue.setText(String.format(Locale.US,"%.1f%%",avg));riskValue.setText(String.valueOf(risk));
        if(studentModel!=null){studentModel.setRowCount(0);for(Student s:store.students)studentModel.addRow(new Object[]{s.id,s.name,s.department,s.email,s.semester,String.format(Locale.US,"%.1f%%",store.attendancePct(s.id)),String.format(Locale.US,"%.1f%%",store.average(s.id))});}
        if(attendanceModel!=null){attendanceModel.setRowCount(0);List<AttendanceRecord> list=new ArrayList<>(store.attendance);list.sort(Comparator.comparing((AttendanceRecord a)->a.date).reversed());for(AttendanceRecord a:list){Student s=store.byId(a.studentId);if(s!=null)attendanceModel.addRow(new Object[]{a.date,a.studentId,s.name,a.status});}}
        if(marksModel!=null){marksModel.setRowCount(0);for(MarkRecord m:store.marks){Student s=store.byId(m.studentId);if(s!=null)marksModel.addRow(new Object[]{m.studentId,s.name,m.subject,m.exam,m.score,m.max,String.format(Locale.US,"%.1f%%",m.score/m.max*100),grade(m.score/m.max*100)});}}
        if(reportModel!=null){reportModel.setRowCount(0);for(Student s:store.students){long n=store.attendanceCount(s.id),pr=store.presentCount(s.id);String status=n==0?"No attendance":store.attendancePct(s.id)<75?"At risk":"On track";reportModel.addRow(new Object[]{s.id,s.name,n,pr,String.format(Locale.US,"%.1f%%",store.attendancePct(s.id)),String.format(Locale.US,"%.1f%%",store.average(s.id)),status});}}
        pages.repaint(); }
    String grade(double p){return p>=90?"A+":p>=80?"A":p>=70?"B":p>=60?"C":p>=50?"D":"Needs support";}
    void info(String s){JOptionPane.showMessageDialog(this,s,"SmartCampus",JOptionPane.INFORMATION_MESSAGE);}
    JTextField field(JPanel p,String label,String value){p.add(new JLabel(label));JTextField f=new JTextField(value,20);p.add(f);return f;}
    void studentDialog(Student old){JPanel form=new JPanel(new GridLayout(0,2,8,9));form.setBorder(new EmptyBorder(12,12,12,12));JTextField id=field(form,"Student ID",old==null?"":""+old.id),name=field(form,"Full name",old==null?"":old.name),dept=field(form,"Department",old==null?"Computer Engineering":old.department),email=field(form,"Email",old==null?"":old.email),sem=field(form,"Semester",old==null?"3":""+old.semester);if(old!=null)id.setEditable(false);int result=JOptionPane.showConfirmDialog(this,form,old==null?"Add student":"Edit student",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);if(result!=JOptionPane.OK_OPTION)return;try{String i=id.getText().trim(),n=name.getText().trim(),d=dept.getText().trim(),em=email.getText().trim();int semester=Integer.parseInt(sem.getText().trim());if(i.isEmpty()||n.isEmpty()||d.isEmpty())throw new IllegalArgumentException("ID, name and department are required.");if(old==null&&store.byId(i)!=null)throw new IllegalArgumentException("That student ID already exists.");if(old==null)store.students.add(new Student(i,n,d,em,semester));else{old.name=n;old.department=d;old.email=em;old.semester=semester;}store.save();refreshAll();}catch(Exception ex){info("Please check the details. "+ex.getMessage());}}
    void deleteStudent(){int r=studentTable.getSelectedRow();if(r<0){info("Select a student first.");return;}String id=String.valueOf(studentModel.getValueAt(r,0));if(JOptionPane.showConfirmDialog(this,"Delete this student and all linked attendance/marks?","Confirm deletion",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;store.students.removeIf(s->s.id.equals(id));store.attendance.removeIf(a->a.studentId.equals(id));store.marks.removeIf(m->m.studentId.equals(id));store.save();refreshAll();}
    void attendanceDialog(){if(store.students.isEmpty()){info("Add a student first.");return;}JComboBox<String> student=new JComboBox<>();for(Student s:store.students)student.addItem(s.id+" — "+s.name);JTextField date=new JTextField(LocalDate.now().toString(),12);JComboBox<String> status=new JComboBox<>(new String[]{"Present","Absent","Late","Excused"});JPanel form=new JPanel(new GridLayout(0,2,8,9));form.add(new JLabel("Student"));form.add(student);form.add(new JLabel("Date (YYYY-MM-DD)"));form.add(date);form.add(new JLabel("Status"));form.add(status);if(JOptionPane.showConfirmDialog(this,form,"Mark attendance",JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION)return;try{String id=((String)student.getSelectedItem()).split(" — ")[0],dt=date.getText().trim();LocalDate.parse(dt);store.attendance.removeIf(a->a.studentId.equals(id)&&a.date.equals(dt));store.attendance.add(new AttendanceRecord(id,dt,(String)status.getSelectedItem()));store.save();refreshAll();}catch(Exception ex){info("Enter a valid date in YYYY-MM-DD format.");}}
    void markAllPresent(){String dt=LocalDate.now().toString();for(Student s:store.students){store.attendance.removeIf(a->a.studentId.equals(s.id)&&a.date.equals(dt));store.attendance.add(new AttendanceRecord(s.id,dt,"Present"));}store.save();refreshAll();info("All students marked present for "+dt+".");}
    void marksDialog(){if(store.students.isEmpty()){info("Add a student first.");return;}JComboBox<String> student=new JComboBox<>();for(Student s:store.students)student.addItem(s.id+" — "+s.name);JTextField subject=new JTextField("Programming",15),exam=new JTextField("Internal Test",15),score=new JTextField("0",8),max=new JTextField("100",8);JPanel form=new JPanel(new GridLayout(0,2,8,9));form.add(new JLabel("Student"));form.add(student);form.add(new JLabel("Subject"));form.add(subject);form.add(new JLabel("Assessment"));form.add(exam);form.add(new JLabel("Score"));form.add(score);form.add(new JLabel("Maximum score"));form.add(max);if(JOptionPane.showConfirmDialog(this,form,"Add assessment marks",JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION)return;try{String id=((String)student.getSelectedItem()).split(" — ")[0];double sc=Double.parseDouble(score.getText()),mx=Double.parseDouble(max.getText());if(subject.getText().trim().isEmpty()||mx<=0||sc<0||sc>mx)throw new IllegalArgumentException();store.marks.add(new MarkRecord(id,subject.getText().trim(),exam.getText().trim(),sc,mx));store.save();refreshAll();}catch(Exception ex){info("Enter a subject and valid scores (0 to maximum score).");}}
    void exportTable(DefaultTableModel m,String defaultName){JFileChooser fc=new JFileChooser();fc.setSelectedFile(new File(defaultName));if(fc.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION)return;File f=fc.getSelectedFile();try(PrintWriter w=new PrintWriter(new OutputStreamWriter(new FileOutputStream(f),java.nio.charset.StandardCharsets.UTF_8))){for(int c=0;c<m.getColumnCount();c++){if(c>0)w.print(',');w.print(csv(m.getColumnName(c)));}w.println();for(int r=0;r<m.getRowCount();r++){for(int c=0;c<m.getColumnCount();c++){if(c>0)w.print(',');w.print(csv(String.valueOf(m.getValueAt(r,c))));}w.println();}info("Report exported to:\n"+f.getAbsolutePath());}catch(Exception ex){info("Export failed: "+ex.getMessage());}}
    String csv(String s){return "\""+s.replace("\"","\"\"")+"\"";}
    void exportAttendance(){exportTable(attendanceModel,"attendance-report.csv");}void exportMarks(){exportTable(marksModel,"performance-report.csv");}void exportReport(){exportTable(reportModel,"student-progress-report.csv");}
    void showSummary(){long atRisk=store.students.stream().filter(s->store.attendanceCount(s.id)>0&&store.attendancePct(s.id)<75).count();StringBuilder b=new StringBuilder("STUDENT PROGRESS SUMMARY\n\nTotal students: ").append(store.students.size()).append("\nAverage attendance: ").append(String.format(Locale.US,"%.1f%%",store.students.stream().mapToDouble(s->store.attendancePct(s.id)).average().orElse(0))).append("\nAverage performance: ").append(String.format(Locale.US,"%.1f%%",store.students.stream().mapToDouble(s->store.average(s.id)).average().orElse(0))).append("\nStudents below 75% attendance: ").append(atRisk).append("\n\nStudents needing attention:\n");for(Student s:store.students)if(store.attendanceCount(s.id)>0&&store.attendancePct(s.id)<75)b.append("• ").append(s.name).append(" (").append(s.id).append("): ").append(String.format(Locale.US,"%.1f%%",store.attendancePct(s.id))).append(" attendance\n");JTextArea area=new JTextArea(b.toString(),14,44);area.setEditable(false);area.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));JOptionPane.showMessageDialog(this,new JScrollPane(area),"Progress summary",JOptionPane.INFORMATION_MESSAGE);}
    class AttendanceChart extends JPanel {AttendanceChart(){setBackground(Color.WHITE);setPreferredSize(new Dimension(300,270));}protected void paintComponent(Graphics g){super.paintComponent(g);Graphics2D x=(Graphics2D)g.create();x.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);int left=38,top=20,bottom=getHeight()-42,w=getWidth()-left-18;for(int v=0;v<=100;v+=25){int y=bottom-(bottom-top)*v/100;x.setColor(new Color(232,237,244));x.drawLine(left,y,left+w,y);x.setColor(muted);x.setFont(new Font("SansSerif",Font.PLAIN,10));x.drawString(v+"%",4,y+4);}int n=Math.min(store.students.size(),8);if(n>0){int slot=w/n;for(int i=0;i<n;i++){Student s=store.students.get(i);int bh=(bottom-top)*(int)store.attendancePct(s.id)/100;int bw=Math.max(8,slot-12),xx=left+i*slot+6,yy=bottom-bh;x.setColor(new Color(74,125,229));x.fillRoundRect(xx,yy,bw,bh,7,7);x.setColor(muted);x.setFont(new Font("SansSerif",Font.PLAIN,9));String lab=s.id;x.drawString(lab,xx-2,bottom+17);}}x.dispose();}}
    class PerformanceChart extends JPanel {PerformanceChart(){setBackground(Color.WHITE);setPreferredSize(new Dimension(300,270));}protected void paintComponent(Graphics g){super.paintComponent(g);Graphics2D x=(Graphics2D)g.create();x.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);int left=38,top=20,bottom=getHeight()-42,w=getWidth()-left-18;for(int v=0;v<=100;v+=25){int y=bottom-(bottom-top)*v/100;x.setColor(new Color(232,237,244));x.drawLine(left,y,left+w,y);x.setColor(muted);x.setFont(new Font("SansSerif",Font.PLAIN,10));x.drawString(v+"%",4,y+4);}int n=Math.min(store.students.size(),8);if(n>0){int slot=w/n;for(int i=0;i<n;i++){Student s=store.students.get(i);int bh=(bottom-top)*(int)store.average(s.id)/100;int bw=Math.max(8,slot-12),xx=left+i*slot+6,yy=bottom-bh;x.setColor(new Color(38,164,130));x.fillRoundRect(xx,yy,bw,bh,7,7);x.setColor(muted);x.setFont(new Font("SansSerif",Font.PLAIN,9));x.drawString(s.id,xx-2,bottom+17);}}x.dispose();}}
}
