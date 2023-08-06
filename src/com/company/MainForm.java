package com.company;

import util.JTableUtils;
import util.SwingUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainForm extends JFrame{
    private JPanel panelMain;
    private JTable tableGameField;
    private JLabel labelStatus;
    private JPanel panelTop;
    private JButton buttonNewGame;
    private JPanel panelTime;
    private JLabel labelTime;
    private JPanel panelCountOfMoney;
    private JLabel labelCountOfMoney;
    private JButton buttonHelp;
    private JPanel panelHelpCount;
    private JLabel labelHelpCount;
    private JPanel panelCountOfEndMoney;
    private JLabel labelCountOfEndMoney;

    private static final int DEFAULT_COL_COUNT = 20;
    private static final int DEFAULT_ROW_COUNT = 15;
    private static final int DEFAULT_COLOUR = 8;
    private static final int DEFAULT_TIME = 120;
    private static final int DEFAULT_HELP_COUNT = 3;
    private static final int DEFAULT_COUNT_END_MONEY = (int) (DEFAULT_COL_COUNT * DEFAULT_ROW_COUNT * 0.6);

    private static final int DEFAULT_GAP = 10;
    private static final int DEFAULT_CELL_SIZE = 30;

    private static final Color[] COLORS = {
            Color.BLUE,
            Color.RED,
            Color.YELLOW,
            Color.GREEN,
            Color.MAGENTA,
            Color.CYAN,
            Color.ORANGE,
            Color.PINK,
            Color.GRAY,
            Color.WHITE,
            Color.BLACK,
            Color.DARK_GRAY
    };

    private final GameParams params = new GameParams(DEFAULT_ROW_COUNT, DEFAULT_COL_COUNT, DEFAULT_COLOUR);
    private final Game game = new Game();

    private int helpCount = DEFAULT_HELP_COUNT;
    private int winMoney = DEFAULT_COUNT_END_MONEY;
    private int time = DEFAULT_TIME;

    private final Timer timer = new Timer(1000, e -> {
        if (time > 0) {
            time--;
            this.labelTime.setText("" + time);
        }
    });

    private final ParamsDialog dialogParams;

    public MainForm() {
        this.setTitle("Три в ряд");
        this.setContentPane(panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        setJMenuBar(createMenuBar());
        this.pack();

        SwingUtils.setShowMessageDefaultErrorHandler();

        labelTime.setFont(new Font("Comic Sans MS", Font.PLAIN, labelTime.getFont().getSize()));
        labelTime.setForeground(new Color(0, 0, 128));
        panelTime.setBackground(Color.LIGHT_GRAY);

        labelCountOfMoney.setFont(new Font("Comic Sans MS", Font.PLAIN, labelCountOfMoney.getFont().getSize()));
        labelCountOfMoney.setForeground(new Color(128, 160, 0));
        panelCountOfMoney.setBackground(Color.LIGHT_GRAY);

        labelCountOfEndMoney.setFont(new Font("Comic Sans MS", Font.PLAIN, labelCountOfEndMoney.getFont().getSize()));
        labelCountOfEndMoney.setForeground(new Color(128, 160, 0));
        panelCountOfEndMoney.setBackground(Color.LIGHT_GRAY);

        labelHelpCount.setFont(new Font("Comic Sans MS", Font.PLAIN, labelHelpCount.getFont().getSize()));
        labelHelpCount.setForeground(new Color(0, 128, 128));
        panelHelpCount.setBackground(Color.LIGHT_GRAY);

        tableGameField.setRowHeight(DEFAULT_CELL_SIZE);
        JTableUtils.initJTableForArray(tableGameField, DEFAULT_CELL_SIZE, false, false,
                false, false);
        tableGameField.setIntercellSpacing(new Dimension(0, 0));
        tableGameField.setEnabled(false);

        tableGameField.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            final class DrawComponent extends Component {
                private int row = 0, column = 0;

                @Override
                public void paint(Graphics gr) {
                    Graphics2D g2d = (Graphics2D) gr;
                    int width = getWidth() - 2;
                    int height = getHeight() - 2;
                    paintCell(row, column, g2d, width, height);
                }
            }

            DrawComponent comp = new DrawComponent();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                comp.row = row;
                comp.column = column;
                return comp;
            }
        });

        newGame();

        updateWindowSize();
        updateView();

        dialogParams = new ParamsDialog(params, tableGameField, e -> newGame());
        buttonNewGame.addActionListener(e -> newGame());

        tableGameField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = tableGameField.rowAtPoint(e.getPoint());
                int col = tableGameField.columnAtPoint(e.getPoint());
                if (SwingUtilities.isLeftMouseButton(e)) {
                    game.leftMouseClick(row, col);
                    updateView();
                }
            }
        });
        buttonHelp.addActionListener(e -> {
            if (helpCount > 0) {
                repaint();
            }
        });
    }

    private JMenuItem createMenuItem(String text, String shortcut, Character mnemonic, ActionListener listener) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(listener);
        if (shortcut != null) {
            menuItem.setAccelerator(KeyStroke.getKeyStroke(shortcut.replace('+', ' ')));
        }
        if (mnemonic != null) {
            menuItem.setMnemonic(mnemonic);
        }
        return menuItem;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBarMain = new JMenuBar();

        JMenu menuGame = new JMenu("Игра");
        menuBarMain.add(menuGame);
        menuGame.add(createMenuItem("Новая", "ctrl+N", null, e -> newGame()));
        menuGame.add(createMenuItem("Параметры", "ctrl+P", null, e -> {
            dialogParams.updateView();
            dialogParams.setVisible(true);
        }));
        menuGame.addSeparator();
        menuGame.add(createMenuItem("Выход", "ctrl+X", null, e -> System.exit(0)));

        JMenu menuView = new JMenu("Вид");
        menuBarMain.add(menuView);
        menuView.add(createMenuItem("Подогнать размер окна", null, null, e -> updateWindowSize()));
        menuView.addSeparator();
        SwingUtils.initLookAndFeelMenu(menuView);

        JMenu menuHelp = new JMenu("Справка");
        menuBarMain.add(menuHelp);
        menuHelp.add(createMenuItem("Правила", "ctrl+R", null, e -> SwingUtils.showInfoMessageBox("""
                        Классический вариант игры «Три в ряд» Уничтожайте поэтапно квадратики, стоящие рядом,
                        одного и того же цвета по два и больше. Сосредоточьтесь в первую очередь на этой задаче.
                        Старайтесь убирать абсолютно все квадратики с поля на каждом уровне, в противном случае
                        Вам придется пользоваться вспомогательными средствами, которых очень мало. Задействуйте
                        кнопки помощи только в самом крайнем случае. За уничтожение квадратиков получайте
                        монетки. Заработайте необходимое количество монет за определённое время.""",
                "Правила")));
        menuHelp.add(createMenuItem("О программе", "ctrl+A", null, e -> SwingUtils.showInfoMessageBox(
                """
                        Игра «Три в ряд»
                        – пример логической игры 1-го курса ФКН ВГУ

                        Автор: Телегина А.С.""",
                "О программе"
        )));

        return menuBarMain;
    }

    private void updateWindowSize() {
        int menuSize = this.getJMenuBar() != null ? this.getJMenuBar().getHeight() : 0;
        SwingUtils.setFixedSize(
                this,
                tableGameField.getWidth() + 2 * DEFAULT_GAP + 60,
                tableGameField.getHeight() + panelMain.getY() + panelTop.getHeight() + labelStatus.getHeight() +
                        menuSize + DEFAULT_GAP + 2 * DEFAULT_GAP + 60
        );
        this.setMaximumSize(null);
        this.setMinimumSize(null);
    }

    private void updateView() {
        labelTime.setText("" + time);
        labelCountOfMoney.setText("" + game.getMoney());
        labelCountOfEndMoney.setText("Необходимо набрать: " + winMoney);
        tableGameField.repaint();
        if (game.getState() == Game.GameState.PLAYING) {
            labelStatus.setForeground(Color.BLACK);
            labelStatus.setText("Игра идет");
            status();
        } else {
            timer.stop();
            labelStatus.setText("");
            if (game.getState() == Game.GameState.WIN) {
                labelStatus.setForeground(Color.RED);
                labelStatus.setText("Победа :-)");
            } else if (game.getState() == Game.GameState.FAIL) {
                labelTime.setText("Время закончилось");
                labelStatus.setForeground(Color.RED);
                labelStatus.setText("Поражение :-(");
            }
        }
    }

    private void paintCell(int row, int column, Graphics2D g2d, int cellWidth, int cellHeight) {
        int cellValue = game.getCell(row, column);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (cellValue <= 0) {
            return;
        }
        Color color = COLORS[cellValue - 1];

        int size = Math.min(cellWidth, cellHeight);
        int bound = (int) Math.round(size * 0.1);

        g2d.setColor(color);
        g2d.fillRoundRect(bound, bound, size - 2 * bound, size - 2 * bound, bound * 3, bound * 3);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRoundRect(bound, bound, size - 2 * bound, size - 2 * bound, bound * 3, bound * 3);
    }

    public void status() {
        if (helpCount == 0) {
            game.haveNoAction();
        }
        if (time > 0) {
            if (time == DEFAULT_TIME - 60) {
                game.addNewLine();
            }
        } else if (time == 0) {
            game.moneyAction();
        }
    }

    public void repaint() {
        game.repaint(params.getRowCount(), params.getColCount(), params.getColourCount());
        JTableUtils.resizeJTable(tableGameField,
                game.getRowCount(), game.getColCount(),
                tableGameField.getRowHeight(), tableGameField.getRowHeight()
        );
        helpCount--;
        labelHelpCount.setText("" + helpCount);
        updateView();
    }

    private void newGame() {
        game.newGame(params.getRowCount(), params.getColCount(), params.getColourCount(), 0,
                (int) (params.getColCount() * params.getRowCount() * 0.7));
        JTableUtils.resizeJTable(tableGameField,
                game.getRowCount(), game.getColCount(),
                tableGameField.getRowHeight(), tableGameField.getRowHeight()
        );
        helpCount = DEFAULT_HELP_COUNT;
        labelHelpCount.setText("" + helpCount);
        int fullMoney = 10 - (int) (params.getColCount() * params.getRowCount() * 0.7 * 0.85) % 10;
        winMoney = (int) (params.getColCount() * params.getRowCount() * 0.7 * 0.85) + fullMoney;
        labelCountOfMoney.setText("" + 0);
        time = DEFAULT_TIME;
        timer.start();
        updateView();
    }
}
