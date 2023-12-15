import com.supermap.data.*;
import com.supermap.realspace.PixelToGlobeMode;
import com.supermap.ui.*;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class MainFrame extends JFrame {
    private SceneControl m_sceneControl;
    private JPanel m_contentPane;
    private JToolBar m_jToolBar;
    private JButton m_measureHeadingButton;
    private Point3Ds m_positions = new Point3Ds();
    private static final String TextTag = "text";
    private static final String GeometryTag = "line";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainFrame thisClass = new MainFrame();
                thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                thisClass.setVisible(true);
            }
        });
    }

    /**
     * 构造函数
     * The constructor function
     */
    public MainFrame() {
        super();
        initialize();
        initializeCultureresources();
    }

    /**
     * 初始化窗体
     * Initialize the form
     */
    private void initialize() {
        // 最大化显示窗体
        // Display the form in maximum size
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        this.setSize(800, 500);
        this.setContentPane(getJContentPane());
        this.setTitle("三维量算");
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            // 在主窗体加载时，初始化SampleRun类型，来完成功能的展现
            // Initialize the SampleRun class when adding the main form
            public void windowOpened(java.awt.event.WindowEvent e) {


            }

            // 在窗体关闭时，需要释放相关的资源
            // Release the resource when the widow closed
            public void windowClosing(java.awt.event.WindowEvent e) {

            }
        });
    }

    private void initializeCultureresources() {
        if (com.supermap.data.Environment.getCurrentCulture().contentEquals("zh-CN")) {
            this.setTitle("三维量算");
        } else {
            this.setTitle("Measure Scene");
        }
    }

    /**
     * 获取m_contentPane
     * Get the m_contentPane
     */
    private JPanel getJContentPane() {
        if (m_contentPane == null) {
            m_contentPane = new JPanel();
            m_contentPane.setLayout(new BorderLayout());
            m_contentPane.add(getJToolBar(), BorderLayout.NORTH);
            m_contentPane.add(getSceneControl(), BorderLayout.CENTER);
        }
        return m_contentPane;
    }

    /**
     * 获取m_jToolBar
     * Get the m_jToolBar
     */
    private JToolBar getJToolBar() {
        if (m_jToolBar == null) {
            m_jToolBar = new JToolBar();
            m_jToolBar
                    .setLayout(new BoxLayout(getJToolBar(), BoxLayout.X_AXIS));
            m_jToolBar.setFloatable(false);
            m_jToolBar.add(getMeasureHeadingButton());
        }
        return m_jToolBar;
    }

    /**
     * 获取m_sceneControl
     * Get the m_sceneControl
     */
    private SceneControl getSceneControl() {
        if (m_sceneControl == null) {
            m_sceneControl = new SceneControl();
        }
        m_sceneControl.addTrackingListener(new Tracking3DListener() {
            @Override
            public void tracking(Tracking3DEvent tracking3DEvent) {
                mytracking(tracking3DEvent);
            }
        });
        m_sceneControl.addTrackedListener(new Tracked3DListener() {
            @Override
            public void tracked(Tracked3DEvent tracked3DEvent) {
                mytracked(tracked3DEvent);
            }
        });
        m_sceneControl.addMouseListener(new MouseInputListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (m_sceneControl.getAction() == Action3D.MeasureDistance && e.getButton() == MouseEvent.BUTTON1) {
                    Point3D p3d = m_sceneControl.getScene().pixelToGlobe(new Point(e.getX(), e.getY()), PixelToGlobeMode.TERRAINANDMODEL);
                    if (m_positions.getCount() == 2) {
                        m_sceneControl.setAction(Action3D.PAN);
                        m_positions.remove(1);
                        m_positions.add(p3d);

                        GeoLine3D gl3d = new GeoLine3D();
                        gl3d.addPart(m_positions);
                        GeoStyle3D gs3d = new GeoStyle3D();
                        gs3d.setAltitudeMode(AltitudeMode.CLAMP_TO_GROUND);
                        gs3d.setLineColor(Color.ORANGE);
                        gs3d.setLineWidth(5);
                        gl3d.setStyle3D(gs3d);
                        int index = m_sceneControl.getScene().getTrackingLayer().indexOf(
                                GeometryTag);
                        if (index != -1) {
                            m_sceneControl.getScene().getTrackingLayer().remove(index);
                        }
                        m_sceneControl.getScene().getTrackingLayer().add(gl3d,
                                GeometryTag);
                        m_sceneControl.getScene().refresh();

                        m_positions.clear();
                    } else if (m_positions.getCount() <= 1) {
                        m_positions.add(p3d);
                    }
                } else if (m_sceneControl.getAction() == Action3D.MeasureDistance && e.getButton() == MouseEvent.BUTTON2) {
                    ClearTrackResult();
                    m_positions.clear();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }

            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
        return m_sceneControl;
    }

    private void ClearTrackResult() {
        m_sceneControl.setAction(Action3D.PAN);
        int index = m_sceneControl.getScene().getTrackingLayer().indexOf(
                GeometryTag);
        if (index != -1) {
            m_sceneControl.getScene().getTrackingLayer().remove(index);
        }
        index = m_sceneControl.getScene().getTrackingLayer().indexOf(
                TextTag);
        if (index != -1) {
            m_sceneControl.getScene().getTrackingLayer().remove(index);
        }
    }

    /**
     * 正在绘制事件
     *
     * @param e
     */
    private void mytracking(Tracking3DEvent e) {
        Point3D p3d = new Point3D(e.getX(), e.getY(), e.getZ());
        if (m_positions.getCount() == 1) {
            m_positions.add(p3d);
            Compute();
        } else if (m_positions.getCount() == 2) {
            m_positions.remove(1);
            m_positions.add(p3d);
            Compute();
        }
    }

    /**
     * 绘制结束事件
     *
     * @param e
     */
    private void mytracked(Tracked3DEvent e) {

    }

    /**
     * 根据绘制两个点计算方向角
     */
    private void Compute() {
        if (m_positions.getCount() == 2) {
            double heading = Complex(m_positions.getItem(0), m_positions.getItem(1));
            String txt = String.format("方向角：" + heading);
            // 将量算结果文本添加到跟踪层
            // Add the text of the result into the tracking layer
            GeoText3D geoText = new GeoText3D(new TextPart3D(txt, m_positions.getItem(1)));
            TextStyle textStyle = new TextStyle();
            textStyle.setForeColor(Color.white);
            textStyle.setFontHeight(15);
            geoText.setTextStyle(textStyle);
            GeoStyle3D style = new GeoStyle3D();
            style.setAltitudeMode(AltitudeMode.ABSOLUTE);
            geoText.setStyle3D(style);
            int index = m_sceneControl.getScene().getTrackingLayer().indexOf(
                    TextTag);
            if (index != -1) {
                m_sceneControl.getScene().getTrackingLayer().remove(index);
            }
            m_sceneControl.getScene().getTrackingLayer().add(geoText,
                    TextTag);
            m_sceneControl.getScene().refresh();
        }
    }

    /**
     * 计算方向角
     * @param start 起始点坐标
     * @param end 终止点坐标
     * @return 方位角，以度为单位
     */
    private double Complex(Point3D start, Point3D end) {
        double numerator = Math.sin(Math.toRadians(end.x - start.x)) * Math.cos(end.y);
        double denominator = Math.cos(Math.toRadians(start.y)) * Math.sin(Math.toRadians(end.y))
                - Math.sin(Math.toRadians(start.y)) * Math.cos(Math.toRadians(end.y)) * Math.cos(Math.toRadians(end.x - start.x));
        double x = Math.atan2(Math.abs(numerator), Math.abs(denominator));
        double result = x;
        /**
         * 判断线的走向，确定方位角
         */
        if (end.x > start.x) {
            if (end.y > start.y) result = x;
            else if (end.y < start.y) result = Math.PI - x;
            else result = Math.PI;
        } else if (end.x < start.x) {
            if (end.y > start.y) result = 2 * Math.PI - x;
            else if (end.y < start.y) result = Math.PI + x;
            else result = Math.PI * 3 / 2;
        } else {
            if (end.y > start.y) result = 0;
            else if (end.y < start.y) result = Math.PI;
            else result = 0;
        }
        return result * 180 / Math.PI;
    }

    /**
     * 获取m_measureHeadingButton
     * Get the m_measureHeadingButton
     */
    private JButton getMeasureHeadingButton() {
        if (m_measureHeadingButton == null) {
            m_measureHeadingButton = new JButton();
            m_measureHeadingButton.setText("量算方位角");
            m_measureHeadingButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    ClearTrackResult();
                    m_sceneControl.setAction(Action3D.MeasureDistance);
                }

            });
        }
        return m_measureHeadingButton;
    }
}