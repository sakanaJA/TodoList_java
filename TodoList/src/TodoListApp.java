import java.awt.BorderLayout;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class TodoListApp {

    private JFrame frame;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField inputField;
    private static final Path FILE_PATH = Paths.get("todolist.txt");

    public TodoListApp() {
        // メインウィンドウの設定
        frame = new JFrame("ToDo List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // テーブルヘッダーの定義
        String[] columns = {"ToDo"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true; // セルを編集可能にする
            }
        };

        // テーブルモデルが変更されたときのリスナーを追加
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                saveToFile(); // テーブルの内容が変わるたびにファイルに保存
            }
        });

        // テーブルとスクロールペインの作成
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        
        // 入力フィールドとボタンの追加
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JButton addButton = new JButton("追加");
        JButton deleteButton = new JButton("削除");

        // 追加ボタンのアクションリスナー
        addButton.addActionListener(e -> {
            String todo = inputField.getText();
            if (!todo.isEmpty()) {
                tableModel.addRow(new Object[]{todo}); // テーブルに新しい行を追加
                inputField.setText(""); // 入力フィールドをクリア
            }
        });

        // 削除ボタンのアクションリスナー
        deleteButton.addActionListener(e -> {
            int selectedIndex = table.getSelectedRow();
            if (selectedIndex != -1) {
                tableModel.removeRow(selectedIndex); // 選択された行を削除
            }
        });

        // ボタンと入力フィールドをパネルに追加
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(addButton, BorderLayout.WEST);
        bottomPanel.add(deleteButton, BorderLayout.EAST);

        // スクロールペインとパネルをフレームに追加
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        //ファイルをロードする
        loadFromFile();
        // ウィンドウのサイズと表示設定
        frame.setSize(400, 300);
        frame.setVisible(true);
    }

    // ファイルへの保存処理
    private void saveToFile() {
        try {
            int rowCount = tableModel.getRowCount();
            
            // テーブルのデータをリストに変換
            List<String> todoList = new java.util.ArrayList<>();
            for (int i = 0; i < rowCount; i++) {
                todoList.add(tableModel.getValueAt(i, 0).toString());
            }

            // リストの内容をファイルに書き込む
            Files.write(FILE_PATH , todoList);
        } catch (Exception e) {
            // エラーメッセージを表示
            JOptionPane.showMessageDialog(frame, "Error saving file: " + e.getMessage());
        }
    }
    private void loadFromFile() {
        if (Files.exists(FILE_PATH)) {
            try {
                List<String> todoList = Files.readAllLines(FILE_PATH);
                for (String todo : todoList) {
                    tableModel.addRow(new Object[]{todo});
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Error loading file: " + e.getMessage());
            }
        }
    }

    // メインメソッド
    public static void main(String[] args) {
        // アプリケーションの起動
        SwingUtilities.invokeLater(() -> new TodoListApp());
    }
}
