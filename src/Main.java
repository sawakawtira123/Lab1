import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main extends JFrame {
    private final UndoManager undoManager = new UndoManager();
    private final SearchResultList searchResultList = new SearchResultList();
    Frame frame = new Frame();
    ImageIcon saveIcon = new ImageIcon("C:\\Users\\dota_\\Desktop\\Laba1\\src\\resource\\images1\\Save.png");
    ImageIcon saveAsIcon = new ImageIcon("C:\\Users\\dota_\\Desktop\\Laba1\\src\\resource\\images 2\\save.png");
    ImageIcon copyIcon = new ImageIcon("C:\\Users\\dota_\\Desktop\\Laba1\\src\\resource\\images1\\Copy.png");
    ImageIcon cutIcon = new ImageIcon("C:\\Users\\dota_\\Desktop\\Laba1\\src\\resource\\images1\\Cut.png");
    ImageIcon helpIcon = new ImageIcon("C:\\Users\\dota_\\Desktop\\Laba1\\src\\resource\\images1\\Help.png");
    ImageIcon newIcon = new ImageIcon("C:\\Users\\dota_\\Desktop\\Laba1\\src\\resource\\images1\\New.png");
    ImageIcon openIcon = new ImageIcon("C:\\Users\\dota_\\Desktop\\Laba1\\src\\resource\\images1\\Open.png");
    ImageIcon redoIcon = new ImageIcon("C:\\Users\\dota_\\Desktop\\Laba1\\src\\resource\\images1\\Redo.png");
    ImageIcon pasteIcon = new ImageIcon("C:\\Users\\dota_\\Desktop\\Laba1\\src\\resource\\images1\\Paste.png");
    ImageIcon undoIcon = new ImageIcon("C:\\Users\\dota_\\Desktop\\Laba1\\src\\resource\\images1\\Undo.png");

    JButton saveAsButton = new JButton() {
        {
            setName("SaveAsButton");
            setIcon(saveAsIcon);
            setVerticalTextPosition(AbstractButton.CENTER);
            setBorderPainted(false);
            setPreferredSize(new Dimension(40, 30));
            // addActionListener(saveAction);
        }
    };
    JButton saveButton = new JButton() {
        {
            setName("SaveButton");
            setIcon(saveIcon);
            setBorderPainted(false);
            setPreferredSize(new Dimension(100, 100));
            // addActionListener(saveAction);
        }
    };
    JButton copyButton = new JButton() {
        private static final long serialVersionUID = 7183327104567601849L;

        {
            setName("CopyButton");
            setIcon(copyIcon);
            setPreferredSize(new Dimension(30, 30));
            setBorderPainted(false);
            addActionListener(e -> {
                textArea.copy();
            });
        }
    };
    JButton cutButton = new JButton() {
        private static final long serialVersionUID = 7183327104567601849L;

        {
            setName("CutButton");
            setIcon(cutIcon);
            setPreferredSize(new Dimension(30, 30));
            setBorderPainted(false);
            addActionListener(e -> {
                textArea.cut();
            });
        }
    };
    JButton helpButton = new JButton() {
        private static final long serialVersionUID = 7183327104567601849L;

        {
            setName("HelpButton");
            setIcon(helpIcon);
            setPreferredSize(new Dimension(30, 30));
            setBorderPainted(false);
            // addActionListener(saveAction);
        }
    };
    JButton newButton = new JButton() {
        private static final long serialVersionUID = 7183327104567601849L;

        {
            setName("NewButton");
            setIcon(newIcon);
            setPreferredSize(new Dimension(30, 30));
            setBorderPainted(false);
            // addActionListener(saveAction);
        }
    };
    JButton pasteButton = new JButton() {
        private static final long serialVersionUID = 7183327104567601849L;

        {
            setName("PasteButton");
            setIcon(pasteIcon);
            setPreferredSize(new Dimension(30, 30));
            setBorderPainted(false);
            addActionListener(e -> {
                textArea.paste();
            });
        }
    };
    JButton undoButton = new JButton() {
        private static final long serialVersionUID = 7183327104567601849L;

        {
            setName("UndoButton");
            setIcon(undoIcon);
            setPreferredSize(new Dimension(30, 30));
            setBorderPainted(false);
            addActionListener(e -> {
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            });
        }
    };
    private JFileChooser fileChooser = null;
    private SearchWorker searchWorker;
    JTextArea textArea = new JTextArea(10, 80) {
        private static final long serialVersionUID = -1598133667203234710L;

        {
            setBounds(10, 10, 280, 270);
            setName("TextArea");
            getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (searchWorker != null) {
                        searchWorker.restart();
                    }
                    searchResultList.clear();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (searchWorker != null) {
                        searchWorker.restart();
                    }
                    searchResultList.clear();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                }
            });
            getDocument().addUndoableEditListener(undoManager);
        }
    };
    final JScrollPane textPane = new JScrollPane(textArea) {
        private static final long serialVersionUID = 6417841791294042219L;

        {
            setName("ScrollPane");
        }
    };
    JButton redoButton = new JButton() {
        private static final long serialVersionUID = 7183327104567601849L;

        {
            setName("redoButton");
            setIcon(redoIcon);
            setPreferredSize(new Dimension(30, 30));
            setBorderPainted(false);
            addActionListener(e -> {
                if (undoManager.canRedo()) {
                    undoManager.redo();
                    searchResultList.clear();
                    if (searchWorker != null) {
                        searchWorker.restart();
                    }
                }
            });
        }
    };
    private int defaultAccelerator;
    private String currentDirectory = System.getProperty("user.home");
    private String currentFileName = "Untitled";
    JButton openButton = new JButton() {
        private static final long serialVersionUID = 7183327104567601849L;

        {
            setName("OpenButton");
            setIcon(openIcon);
            setPreferredSize(new Dimension(30, 30));
            setBorderPainted(false);
            addActionListener(e -> {
                FileDialog fileDialog = new FileDialog(frame, "Open File", FileDialog.LOAD);
                fileDialog.setMultipleMode(false);
                fileDialog.setDirectory(currentDirectory);
                fileDialog.setVisible(true);
                currentDirectory = fileDialog.getDirectory();
                if (fileDialog.getFile() != null) {
                    currentFileName = fileDialog.getFile();
                    String fileName = currentDirectory + currentFileName;
                    try {
                        textArea.setText(new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8));
                        undoManager.discardAllEdits();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            });
        }
    };
    public Main() {
        super("Compiler");
        JPanel contents = new JPanel();
        JMenuBar menuBar = new JMenuBar();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 400);
        menuBar.add(createFileMenu());
        menuBar.add(createCodeMenu());
        menuBar.add(createTextMenu());
        menuBar.add(createActionMenu());
        menuBar.add(createReferenceMenu());
        contents.add(cutButton);
        contents.add(saveAsButton);
        contents.add(openButton);
        contents.add(copyButton);
        contents.add(newButton);
        contents.add(redoButton);
        contents.add(undoButton);
        contents.add(pasteButton);
        contents.add(textPane);
        setContentPane(contents);
        // Создание экземпляра JFileChooser
        fileChooser = new JFileChooser();
        setVisible(true);
        setJMenuBar(menuBar);
    }

    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        new Main();
    }

    public JMenu createFileMenu() {
        JMenu file = new JMenu("Файл");
        JMenuItem create = new JMenuItem("Создать");
        JMenuItem open = new JMenuItem("Открыть");
        JMenuItem save = new JMenuItem("Сохранить");
        JMenuItem saveAs = new JMenuItem("Сохранить как");
        JMenuItem exit = new JMenuItem(new ExitAction());
        CodeMenu(file, create, open, save, saveAs, exit);

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog fileDialog = new FileDialog(frame, "Save As ...", FileDialog.SAVE);
                fileDialog.setDirectory(currentDirectory);
                fileDialog.setFile(currentFileName);
                fileDialog.setVisible(true);
                currentDirectory = fileDialog.getDirectory();
                if (fileDialog.getFile() != null) {
                    currentFileName = fileDialog.getFile();
                    String fileName = currentDirectory + currentFileName;
                    try {
                        Files.write(Paths.get(fileName), textArea.getText().getBytes(StandardCharsets.UTF_8));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }

            ;
        });
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog fileDialog = new FileDialog(frame, "Open File", FileDialog.LOAD);
                fileDialog.setMultipleMode(false);
                fileDialog.setDirectory(currentDirectory);
                fileDialog.setVisible(true);
                currentDirectory = fileDialog.getDirectory();
                if (fileDialog.getFile() != null) {
                    currentFileName = fileDialog.getFile();
                    String fileName = currentDirectory + currentFileName;
                    try {
                        textArea.setText(new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8));
                        undoManager.discardAllEdits();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }

            ;
        });
        return file;
    }

    public JMenu createCodeMenu() {
        JMenu code = new JMenu("Правка");
        JMenuItem menuUndo = new JMenuItem("Отменить") {
            private static final long serialVersionUID = -843926481964895754L;

            {
                addActionListener(e -> {
                    if (undoManager.canUndo()) {
                        undoManager.undo();
                    }
                });
                setAccelerator(
                        KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
            }
        };

        JMenuItem menuRedo = new JMenuItem("Повторить") {
            private static final long serialVersionUID = 7631745700972370457L;

            {
                addActionListener(e -> {
                    if (undoManager.canRedo()) {
                        undoManager.redo();
                        searchResultList.clear();
                        if (searchWorker != null) {
                            searchWorker.restart();
                        }
                    }
                });
                setAccelerator(
                        KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));

            }
        };
        JMenuItem cut = new JMenuItem("Вырезать") {
            {
                addActionListener(e -> {
                    textArea.cut();
                });
                setAccelerator(
                        KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
            }
        };
        JMenuItem copy = new JMenuItem("Копировать") {
            {
                addActionListener(e -> {
                    textArea.copy();
                });
                setAccelerator(
                        KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
            }
        };
        JMenuItem paste = new JMenuItem("Вставить") {
            {
                addActionListener(e -> {
                    textArea.paste();
                });
                setAccelerator(
                        KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
            }
        };
        JMenuItem delete = new JMenuItem("Удалить") {
            {
                addActionListener(e -> {
                    textArea.removeAll();
                });
                setAccelerator(
                        KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.CTRL_DOWN_MASK));
            }
        };
        JMenuItem selectAll = new JMenuItem("Выделить все") {
            {
                addActionListener(e -> {
                    textArea.selectAll();
                });
                setAccelerator(
                        KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
            }
        };
        CodeMenu(code, menuUndo, menuRedo, cut, copy, paste);
        code.addSeparator();
        code.add(delete);
        code.addSeparator();
        code.add(selectAll);
        return code;
    }

    public JMenu createTextMenu() {
        JMenu text = new JMenu("Текст");
        JMenuItem task = new JMenuItem("Постановка задачи");
        JMenuItem gramm = new JMenuItem("Грамматика");
        JMenuItem grammClass = new JMenuItem("Классификация грамматики");
        JMenuItem method = new JMenuItem("Метод анализа");
        JMenuItem refactor = new JMenuItem("Диагностика и нейтролизация ошибок");
        JMenuItem list = new JMenuItem("Список литературы");
        JMenuItem code = new JMenuItem("Исходный код программы");
        textMenu(text, task, gramm, grammClass, method, refactor);
        text.addSeparator();
        text.add(list);
        text.addSeparator();
        text.add(code);
        return text;
    }

    private void textMenu(JMenu text, JMenuItem task, JMenuItem gramm, JMenuItem grammClass, JMenuItem method, JMenuItem refactor) {
        text.add(task);
        text.addSeparator();
        text.add(grammClass);
        text.addSeparator();
        text.add(gramm);
        text.addSeparator();
        text.add(method);
        text.addSeparator();
        text.add(refactor);
    }

    private void CodeMenu(JMenu code, JMenuItem menuRedo, JMenuItem menuUndo, JMenuItem cut, JMenuItem copy, JMenuItem paste) {
        textMenu(code, menuRedo, menuUndo, cut, copy, paste);
    }

    public JMenu createReferenceMenu() {
        JMenu reference = new JMenu("Справка");
        JMenuItem callReference = new JMenuItem("Вызов справки");
        JMenuItem about = new JMenuItem("Опрограмме");
        reference.add(callReference);
        reference.addSeparator();
        reference.add(about);
        return reference;
    }

    public JMenu createActionMenu() {
        return new JMenu("Пуск");
    }


}
