package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// ===============================
// Custom Exception
// ===============================
class KuantumCokusuException extends Exception {
    private String nesneId;

    public KuantumCokusuException(String id) {
        super("KUANTUM COKUSU! Nesne patladi: " + id);
        this.nesneId = id;
    }

    public String getNesneId() {
        return nesneId;
    }
}

// ===============================
// Interface
// ===============================
interface IKritik {
    void acilDurumSogutmasi();
    default boolean isKritik() {
        return true;
    }
}

// ===============================
// Abstract Class
// ===============================
abstract class KuantumNesnesi {
    private String id;
    private double stabilite;
    private int tehlikeSeviyesi;

    public KuantumNesnesi(String id, double stabilite, int tehlikeSeviyesi) {
        this.id = id;
        setStabilite(stabilite);
        setTehlikeSeviyesi(tehlikeSeviyesi);
    }

    public String getId() {
        return id;
    }

    public double getStabilite() {
        return stabilite;
    }

    public void setStabilite(double stabilite) {
        if (stabilite < 0) this.stabilite = 0;
        else if (stabilite > 100) this.stabilite = 100;
        else this.stabilite = stabilite;
    }

    public int getTehlikeSeviyesi() {
        return tehlikeSeviyesi;
    }

    public void setTehlikeSeviyesi(int tehlikeSeviyesi) {
        if (tehlikeSeviyesi < 1 || tehlikeSeviyesi > 10) {
            throw new IllegalArgumentException("Tehlike seviyesi 1-10 arasi olmalidir!");
        }
        this.tehlikeSeviyesi = tehlikeSeviyesi;
    }

    protected void stabiliteKontrol() throws KuantumCokusuException {
        if (stabilite <= 0) {
            throw new KuantumCokusuException(id);
        }
    }

    public String durumBilgisi() {
        return String.format("ID: %s | Stabilite: %.1f | Tehlike: %d | Tip: %s",
                id, stabilite, tehlikeSeviyesi, this.getClass().getSimpleName());
    }

    public abstract String analizEt() throws KuantumCokusuException;

    public boolean isKritik() {
        return this instanceof IKritik;
    }

    public String getTipAdi() {
        return this.getClass().getSimpleName();
    }
}

// ===============================
// Concrete Classes
// ===============================
class VeriPaketi extends KuantumNesnesi {
    public VeriPaketi(String id, double stabilite, int tehlike) {
        super(id, stabilite, tehlike);
    }

    @Override
    public String analizEt() throws KuantumCokusuException {
        setStabilite(getStabilite() - 5);
        stabiliteKontrol();
        return "Veri icerigi okundu.";
    }
}

class KaranlikMadde extends KuantumNesnesi implements IKritik {
    public KaranlikMadde(String id, double stabilite, int tehlike) {
        super(id, stabilite, tehlike);
    }

    @Override
    public String analizEt() throws KuantumCokusuException {
        setStabilite(getStabilite() - 15);
        stabiliteKontrol();
        return "Karanlik madde analiz ediliyor...";
    }

    @Override
    public void acilDurumSogutmasi() {
        setStabilite(getStabilite() + 50);
    }
}

class AntiMadde extends KuantumNesnesi implements IKritik {
    public AntiMadde(String id, double stabilite, int tehlike) {
        super(id, stabilite, tehlike);
    }

    @Override
    public String analizEt() throws KuantumCokusuException {
        setStabilite(getStabilite() - 25);
        stabiliteKontrol();
        return "EVRENIN DOKUSU TITRIYOR...";
    }

    @Override
    public void acilDurumSogutmasi() {
        setStabilite(getStabilite() + 50);
    }
}

// ===============================
// JavaFX GUI Application
// ===============================
public class KuantumAmbarGUI extends Application {

    private List<KuantumNesnesi> envanter = new ArrayList<>();
    private Random random = new Random();
    private TextArea logArea;
    private VBox inventoryBox;
    private TextField idField;
    private Label toplamLabel;
    private Label kritikLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Kuantum Ambari Kontrol Paneli");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #0a0a0a, #1a1a2e);");

        VBox header = createHeader();
        root.setTop(header);

        VBox leftPanel = createControlPanel();
        root.setLeft(leftPanel);

        ScrollPane inventoryScroll = createInventoryPanel();
        root.setCenter(inventoryScroll);

        VBox logPanel = createLogPanel();
        root.setRight(logPanel);

        Scene scene = new Scene(root, 1400, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

        logMesaj("Sistem baslatildi. Kuantum Ambari hazir!", "SUCCESS");
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #16213e; -fx-border-color: #00ff41; -fx-border-width: 2;");

        Label title = new Label("KUANTUM AMBARI KONTROL PANELI");
        title.setFont(Font.font("Monospaced", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#00ff41"));

        Label subtitle = new Label("Omega Sektoru - Vardiya Kontrol Sistemi");
        subtitle.setFont(Font.font("Monospaced", FontWeight.NORMAL, 12));
        subtitle.setTextFill(Color.web("#00d9ff"));

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private VBox createControlPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setPrefWidth(300);
        panel.setStyle("-fx-background-color: #16213e; -fx-border-color: #4ecdc4; -fx-border-width: 2;");

        Label title = new Label("KONTROL MERKEZI");
        title.setFont(Font.font("Monospaced", FontWeight.BOLD, 14));
        title.setTextFill(Color.web("#ff6b6b"));
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);

        Button btnEkle = createButton("Yeni Nesne Ekle", "#4ecdc4", "#0f3443");
        btnEkle.setOnAction(e -> yeniNesneEkle());

        Button btnListele = createButton("Envanteri Goster", "#95e1d3", "#0f3443");
        btnListele.setOnAction(e -> envanterGoster());

        Label idLabel = new Label("Nesne ID Giriniz:");
        idLabel.setFont(Font.font("Monospaced", 11));
        idLabel.setTextFill(Color.WHITE);

        idField = new TextField();
        idField.setPromptText("Ornek: QN-123");
        idField.setStyle("-fx-background-color: #0f3443; -fx-text-fill: #00ff41; " +
                "-fx-prompt-text-fill: #006600; -fx-font-family: 'Monospaced';");

        Button btnAnaliz = createButton("Nesneyi Analiz Et", "#f38181", "#0f3443");
        btnAnaliz.setOnAction(e -> nesneAnaliz());

        Button btnSogutma = createButton("Acil Durum Sogutmasi", "#aa96da", "#0f3443");
        btnSogutma.setOnAction(e -> acilSogutma());

        Button btnCikis = createButton("Guvenli Cikis", "#ff6b6b", "white");
        btnCikis.setOnAction(e -> guvenliCikis());

        VBox statusBox = new VBox(10);
        statusBox.setStyle("-fx-background-color: #0f3443; -fx-border-color: #00d9ff; -fx-border-width: 2; -fx-padding: 15;");
        statusBox.setAlignment(Pos.CENTER);

        toplamLabel = createStatusLabel("TOPLAM: 0");
        kritikLabel = createStatusLabel("KRITIK: 0");
        Label durumLabel = createStatusLabel("DURUM: AKTIF");
        durumLabel.setTextFill(Color.web("#4ecdc4"));

        statusBox.getChildren().addAll(toplamLabel, kritikLabel, durumLabel);

        panel.getChildren().addAll(title, btnEkle, btnListele, idLabel, idField,
                btnAnaliz, btnSogutma, btnCikis, statusBox);
        return panel;
    }

    private ScrollPane createInventoryPanel() {
        inventoryBox = new VBox(15);
        inventoryBox.setPadding(new Insets(20));
        inventoryBox.setStyle("-fx-background-color: #1a1a2e;");

        Label title = new Label("ENVANTER LISTESI");
        title.setFont(Font.font("Monospaced", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#95e1d3"));
        inventoryBox.getChildren().add(title);

        ScrollPane scroll = new ScrollPane(inventoryBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        return scroll;
    }

    private VBox createLogPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(20));
        panel.setPrefWidth(400);
        panel.setStyle("-fx-background-color: #16213e; -fx-border-color: #ffb400; -fx-border-width: 2;");

        Label title = new Label("SISTEM LOGU");
        title.setFont(Font.font("Monospaced", FontWeight.BOLD, 14));
        title.setTextFill(Color.web("#ffb400"));

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefHeight(700);
        logArea.setStyle("-fx-control-inner-background: #0f3443; -fx-text-fill: #00ff41; " +
                "-fx-font-family: 'Monospaced'; -fx-font-size: 11;");

        panel.getChildren().addAll(title, logArea);
        return panel;
    }

    private Button createButton(String text, String bgColor, String textColor) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setFont(Font.font("Monospaced", FontWeight.BOLD, 11));
        btn.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: %s; " +
                        "-fx-border-color: %s; -fx-border-width: 2; -fx-padding: 12;",
                bgColor, textColor, bgColor));

        // Hover efekti için daha güvenli yöntem
        String originalStyle = btn.getStyle();
        btn.setOnMouseEntered(e -> btn.setStyle(originalStyle + "-fx-translate-y: -2;"));
        btn.setOnMouseExited(e -> btn.setStyle(originalStyle + "-fx-translate-y: 0;"));

        return btn;
    }

    private Label createStatusLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Monospaced", FontWeight.BOLD, 13));
        label.setTextFill(Color.web("#00ff41"));
        return label;
    }

    private void logMesaj(String mesaj, String tip) {
        String zaman = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String prefix = switch (tip) {
            case "ERROR" -> "[HATA]";
            case "WARNING" -> "[UYARI]";
            case "SUCCESS" -> "[BASARILI]";
            default -> "[BILGI]";
        };
        logArea.appendText(String.format("[%s] %s %s\n", zaman, prefix, mesaj));
    }

    private void guncelleIstatistik() {
        int toplam = envanter.size();
        int kritik = (int) envanter.stream().filter(KuantumNesnesi::isKritik).count();
        toplamLabel.setText("TOPLAM: " + toplam);
        kritikLabel.setText("KRITIK: " + kritik);
    }

    private String rastgeleID() {
        return "QN-" + random.nextInt(10000);
    }

    private void yeniNesneEkle() {
        try {
            int tip = random.nextInt(3);
            String id = rastgeleID();
            double stabilite = 60 + random.nextInt(41);
            int tehlike = 1 + random.nextInt(10);

            KuantumNesnesi nesne;
            if (tip == 0) nesne = new VeriPaketi(id, stabilite, tehlike);
            else if (tip == 1) nesne = new KaranlikMadde(id, stabilite, tehlike);
            else nesne = new AntiMadde(id, stabilite, tehlike);

            envanter.add(nesne);
            logMesaj("Yeni nesne eklendi: " + nesne.durumBilgisi(), "SUCCESS");
            guncelleIstatistik();
            envanterGoster();
        } catch (Exception e) {
            logMesaj("Hata: " + e.getMessage(), "ERROR");
        }
    }

    private void envanterGoster() {
        inventoryBox.getChildren().clear();

        Label title = new Label("ENVANTER LISTESI");
        title.setFont(Font.font("Monospaced", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#95e1d3"));
        inventoryBox.getChildren().add(title);

        if (envanter.isEmpty()) {
            Label empty = new Label("Envanter bos.");
            empty.setTextFill(Color.web("#00d9ff"));
            empty.setFont(Font.font("Monospaced", 12));
            inventoryBox.getChildren().add(empty);
            return;
        }

        for (KuantumNesnesi nesne : envanter) {
            VBox card = createInventoryCard(nesne);
            inventoryBox.getChildren().add(card);
        }
    }

    private VBox createInventoryCard(KuantumNesnesi nesne) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));

        String borderColor = switch (nesne.getTipAdi()) {
            case "VeriPaketi" -> "#4ecdc4";
            case "KaranlikMadde" -> "#f38181";
            case "AntiMadde" -> "#ff6b6b";
            default -> "#00ff41";
        };

        card.setStyle(String.format("-fx-background-color: #0f3443; -fx-border-color: %s; " +
                        "-fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;",
                borderColor));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);

        Label idLabel = new Label(nesne.getId());
        idLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 13));
        idLabel.setTextFill(Color.web("#00ff41"));

        Label tipLabel = new Label("[" + nesne.getTipAdi() + "]");
        tipLabel.setFont(Font.font("Monospaced", 10));
        tipLabel.setTextFill(Color.web("#00d9ff"));

        header.getChildren().addAll(idLabel, tipLabel);

        double stabilite = nesne.getStabilite();
        ProgressBar progressBar = new ProgressBar(stabilite / 100.0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(20);

        String barColor = stabilite >= 60 ? "#4ecdc4" : stabilite >= 30 ? "#ffb400" : "#ff6b6b";
        progressBar.setStyle(String.format("-fx-accent: %s;", barColor));

        Label stabiliteLabel = new Label(String.format("Stabilite: %.1f%%", stabilite));
        stabiliteLabel.setFont(Font.font("Monospaced", 11));
        stabiliteLabel.setTextFill(Color.web("#00d9ff"));

        Label tehlikeLabel = new Label(String.format("Tehlike Seviyesi: %d/10", nesne.getTehlikeSeviyesi()));
        tehlikeLabel.setFont(Font.font("Monospaced", 10));
        tehlikeLabel.setTextFill(Color.web("#00d9ff"));

        card.getChildren().addAll(header, stabiliteLabel, progressBar, tehlikeLabel);

        if (nesne.isKritik()) {
            Label kritikLabel = new Label("[KRITIK MADDE]");
            kritikLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 9));
            kritikLabel.setTextFill(Color.web("#ff6b6b"));
            card.getChildren().add(kritikLabel);
        }

        return card;
    }

    private void nesneAnaliz() {
        try {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                logMesaj("Lutfen bir ID giriniz!", "WARNING");
                return;
            }

            KuantumNesnesi nesne = envanter.stream()
                    .filter(n -> n.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (nesne == null) {
                logMesaj("Nesne bulunamadi: " + id, "ERROR");
                return;
            }

            String mesaj = nesne.analizEt();
            logMesaj("Analiz: " + mesaj, "SUCCESS");
            logMesaj(nesne.durumBilgisi(), "SUCCESS");

            if (nesne.getStabilite() < 30) {
                logMesaj("UYARI: " + id + " kritik stabilite seviyesinde!", "WARNING");
            }

            envanterGoster();

        } catch (KuantumCokusuException e) {
            sistemCoktu(e.getMessage());
        } catch (Exception e) {
            logMesaj("Hata: " + e.getMessage(), "ERROR");
        }
    }

    private void acilSogutma() {
        try {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                logMesaj("Lutfen bir ID giriniz!", "WARNING");
                return;
            }

            KuantumNesnesi nesne = envanter.stream()
                    .filter(n -> n.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (nesne == null) {
                logMesaj("Nesne bulunamadi: " + id, "ERROR");
                return;
            }

            if (nesne instanceof IKritik) {
                ((IKritik) nesne).acilDurumSogutmasi();
                logMesaj("Acil sogutma uygulandi: " + id, "SUCCESS");
                logMesaj(nesne.durumBilgisi(), "SUCCESS");
                envanterGoster();
            } else {
                logMesaj("Bu nesne sogutulamaz! (IKritik degil)", "WARNING");
            }

        } catch (Exception e) {
            logMesaj("Hata: " + e.getMessage(), "ERROR");
        }
    }

    private void sistemCoktu(String mesaj) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("KUANTUM COKUSU!");
        alert.setHeaderText("SISTEM COKTU! TAHLIYE BASLATILIYOR...");
        alert.setContentText(mesaj + "\n\nSistem kapanacak...");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #16213e;");
        dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #ff6b6b;");
        dialogPane.lookup(".content").setStyle("-fx-text-fill: #00ff41; -fx-font-family: 'Monospaced';");

        alert.showAndWait();
        Platform.exit();
    }

    private void guvenliCikis() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cikis");
        alert.setHeaderText("Guvenli cikis yapmak istiyor musunuz?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            logMesaj("Guvenli cikis yapiliyor...", "SUCCESS");
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}