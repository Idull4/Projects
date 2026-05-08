import model.TeaMakerModel;
import view.TeaMakerView;
import controller.TeaMakerController;

public class Demo {
    public static void main(String[] args) {
        try {

            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}


        TeaMakerModel model = new TeaMakerModel();
        TeaMakerView view = new TeaMakerView(model);
        TeaMakerController controller = new TeaMakerController(model, view);


        java.awt.EventQueue.invokeLater(() -> {
            view.setVisible(true);
        });
    }
}