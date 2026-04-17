package simulation;

// Импорты для JavaFX - библиотеки для создания графического интерфейса
import javafx.animation.AnimationTimer; // Класс для создания анимации (автоматическое повторение действий)
import javafx.animation.FadeTransition; // Анимация плавного появления/исчезновения
import javafx.animation.ScaleTransition; // Анимация изменения размера
import javafx.animation.TranslateTransition; // Анимация перемещения
import javafx.application.Application; // Базовый класс для JavaFX приложений
import javafx.geometry.Insets; // Класс для задания отступов (margins/padding)
import javafx.geometry.Pos; // Класс для выравнивания элементов
import javafx.scene.Scene; // Контейнер для всех элементов окна
import javafx.scene.control.Button; // Кнопка
import javafx.scene.control.Label; // Текстовая метка
import javafx.scene.layout.BorderPane; // Контейнер с 5 областями (верх, низ, лево, право, центр)
import javafx.scene.layout.GridPane; // Контейнер в виде сетки (таблицы)
import javafx.scene.layout.HBox; // Контейнер для горизонтального размещения элементов
import javafx.scene.layout.Pane; // Простой контейнер для свободного позиционирования
import javafx.scene.layout.StackPane; // Контейнер для наложения элементов друг на друга
import javafx.stage.Stage; // Окно приложения
import javafx.util.Duration; // Класс для задания длительности анимации

// Импорты классов нашей симуляции
import simulation.Entity.Berries;
import simulation.Entity.Entity; // Базовый класс для всех объектов
import simulation.Entity.Tree; // Класс дерева
import simulation.Entity.Iwe; // Класс ивы
import simulation.backend.Position; // Класс для хранения координат (x, y)
import simulation.backend.Simulation; // Основной класс симуляции
import simulation.util.Config;

import java.util.HashMap; // Класс для хранения пар ключ-значение
import java.util.Iterator; // Интерфейс для перебора элементов коллекции
import java.util.Map; // Интерфейс для работы с картой (словарем) ключ-значение

// Главный класс приложения, наследуется от Application (это требование JavaFX)
public class SimulationApp extends Application {

    // Поля класса (переменные, которые доступны во всех методах класса)
    
    private GridPane grid; // Сетка для отображения игрового поля (фон с черными клетками)
    private Pane entityLayer; // Слой для отображения сущностей (деревья, ивы и т.д.)
    private Simulation simulation; // Объект симуляции (содержит всю логику игры)
    private AnimationTimer timer; // Таймер для автоматического запуска шагов симуляции
    private Label cycleLabel; // Текстовая метка для отображения номера цикла
    
    // Map для хранения соответствия между сущностями и их визуальными представлениями (Label)
    // Ключ - объект Entity, значение - Label с символом этой сущности
    private Map<Entity, Label> entityNodes = new HashMap<>();
    
    // Map для хранения предыдущих позиций сущностей (нужно для анимации перемещения)
    // Ключ - объект Entity, значение - его предыдущая позиция Position
    private Map<Entity, Position> previousPositions = new HashMap<>();

    // Размеры игрового поля
    private int width = Config.getWidth(); // Ширина поля (количество клеток по горизонтали)
    private int height = Config.getHeigh(); // Высота поля (количество клеток по вертикали)
    
    // Размер одной клетки в пикселях
    private final int CELL_SIZE = 40;

    // Метод start() - точка входа для JavaFX приложения
    // Вызывается автоматически при запуске приложения
    // primaryStage - главное окно приложения (создается JavaFX автоматически)
    @Override
    public void start(Stage primaryStage) {
        // Создаем корневой контейнер BorderPane (разделен на 5 областей)
        BorderPane root = new BorderPane();
        // Устанавливаем отступы 10 пикселей со всех сторон
        root.setPadding(new Insets(10));

        // Создаем объект симуляции с заданными размерами поля
        // В конструкторе Simulation создаются все объекты (деревья, ивы и т.д.)
        simulation = new Simulation(width, height);

        // Создаем сетку GridPane для визуализации игрового поля (черный фон)
        grid = new GridPane();
        // Устанавливаем горизонтальный зазор между клетками (0 пикселей)
        grid.setHgap(0);
        // Устанавливаем вертикальный зазор между клетками (0 пикселей)
        grid.setVgap(0);
        // Выравниваем сетку по левому верхнему углу
        grid.setAlignment(Pos.TOP_LEFT);
        // Заполняем сетку пустыми черными клетками
        initializeGrid();

        // Создаем слой для сущностей (Pane позволяет свободно позиционировать элементы)
        entityLayer = new Pane();
        // Устанавливаем размер слоя равным размеру игрового поля
        entityLayer.setPrefSize(width * CELL_SIZE, height * CELL_SIZE);
        // Делаем слой прозрачным для мыши (клики проходят сквозь него)
        entityLayer.setMouseTransparent(true);

        // Создаем StackPane для наложения слоя сущностей поверх сетки
        // StackPane размещает элементы друг над другом (grid внизу, entityLayer сверху)
        StackPane stack = new StackPane(grid, entityLayer);
        // Выравниваем содержимое по левому верхнему углу
        stack.setAlignment(Pos.TOP_LEFT);

        // Размещаем StackPane в центральной области BorderPane
        root.setCenter(stack);

        // Создаем текстовую метку для отображения номера текущего цикла
        cycleLabel = new Label("Цикл: 0");
        // Устанавливаем стиль: размер шрифта 16px, жирный текст
        cycleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Создаем кнопки управления симуляцией
        Button startButton = new Button("Старт"); // Кнопка запуска автоматической симуляции
        Button pauseButton = new Button("Пауза"); // Кнопка остановки автоматической симуляции
        Button stepButton = new Button("Шаг"); // Кнопка выполнения одного шага вручную
        Button resetButton = new Button("Сброс"); // Кнопка сброса симуляции (начать заново)

        // Привязываем обработчики событий к кнопкам
        // При нажатии на "Старт" запускаем таймер
        startButton.setOnAction(e -> timer.start());
        // При нажатии на "Пауза" останавливаем таймер
        pauseButton.setOnAction(e -> timer.stop());
        // При нажатии на "Шаг" выполняем один шаг симуляции
        stepButton.setOnAction(e -> nextStep());
        // При нажатии на "Сброс" сбрасываем симуляцию
        resetButton.setOnAction(e -> resetSimulation());

        // Создаем горизонтальный контейнер HBox для кнопок управления
        // 10 - расстояние между элементами внутри контейнера (в пикселях)
        HBox controls = new HBox(10, cycleLabel, startButton, pauseButton, stepButton, resetButton);
        // Устанавливаем отступ сверху 10 пикселей
        controls.setPadding(new Insets(10, 0, 0, 0));

        // Размещаем контейнер с кнопками в нижней области BorderPane
        root.setBottom(controls);

        // Создаем таймер для автоматического выполнения шагов симуляции
        // AnimationTimer - специальный класс JavaFX для создания анимации
        timer = new AnimationTimer() {
            // Переменная для хранения времени последнего обновления (в наносекундах)
            private long last = 0;
            
            // Метод handle() вызывается JavaFX примерно 60 раз в секунду
            // now - текущее время в наносекундах
            @Override
            public void handle(long now) {
                // Проверяем, прошло ли 500 миллисекунд (0.5 секунды) с последнего обновления
                // 500_000_000 наносекунд = 500 миллисекунд
                if (now - last > 500_000_000) {
                    // Выполняем один шаг симуляции
                    nextStep();
                    // Запоминаем текущее время как время последнего обновления
                    last = now;
                }
            }
        };

        // Создаем сцену (Scene) - контейнер для всех элементов интерфейса
        // Вычисляем размер окна на основе размеров игрового поля
        Scene scene = new Scene(root, width * CELL_SIZE + 20, height * CELL_SIZE + 100);
        // Устанавливаем сцену в окно
        primaryStage.setScene(scene);
        // Устанавливаем заголовок окна
        primaryStage.setTitle("Симуляция животного мира");
        // Запрещаем изменение размера окна (чтобы сетка не растягивалась)
        primaryStage.setResizable(false);
        // Отображаем окно на экране
        primaryStage.show();

        // Выполняем первое обновление визуализации (отображаем начальное состояние)
        updateGridAnimated();
    }

    // Метод для инициализации сетки (создание пустых черных клеток)
    private void initializeGrid() {
        // Двойной цикл для создания клеток: проходим по всем строкам и столбцам
        // i - номер строки (от 0 до height-1)
        for (int i = 0; i < height; i++) {
            // j - номер столбца (от 0 до width-1)
            for (int j = 0; j < width; j++) {
                // Создаем пустую текстовую метку (черная клетка фона)
                Label cell = new Label();
                // Устанавливаем стиль клетки:
                // -fx-background-color: черный фон
                // -fx-border-color: темно-серая граница (#333)
                // Все размеры фиксированы на CELL_SIZE (40 пикселей)
                cell.setStyle("-fx-background-color: black; -fx-border-color: #333; " +
                        "-fx-min-width:" + CELL_SIZE + "; -fx-min-height:" + CELL_SIZE + "; " +
                        "-fx-max-width:" + CELL_SIZE + "; -fx-max-height:" + CELL_SIZE + ";");
                // Добавляем клетку в сетку на позицию (j, i)
                grid.add(cell, j, i);
            }
        }
    }

    // Метод для создания визуального представления сущности (Label с символом)
    // Параметр e - объект Entity (дерево, ива и т.д.)
    // Возвращает Label с символом и цветом, соответствующим типу сущности
    private Label createNode(Entity e) {
        // Получаем символ сущности и удаляем ANSI escape-коды
        // Регулярное выражение \u001B\\[[0-9;]*m находит все ANSI коды цвета
        Label node = new Label(e.getSymbol().replaceAll("\\u001B\\[[0-9;]*m", ""));

        // Определяем цвет в зависимости от типа сущности
        String color = "#FFFFFF"; // По умолчанию желтый
        if (e instanceof Tree) color = "#00FF00"; // Дерево - зеленый
        else if (e instanceof Iwe) color = "#CCCCCC"; // Ива - светло-серый
        else if (e instanceof Berries) color = "#FD0000";

        // Устанавливаем стиль: размер шрифта 24px, цвет текста
        node.setStyle("-fx-font-size:24px; -fx-text-fill:" + color + ";");
        return node;
    }

    // Метод для обновления визуализации с анимацией
    // Обрабатывает появление, перемещение и исчезновение сущностей
    private void updateGridAnimated() {
        // Получаем текущую карту объектов из симуляции
        Map<Position, Entity> map = simulation.getObjsMap();

        // Проходим по всем сущностям в текущем состоянии симуляции
        for (Map.Entry<Position, Entity> entry : map.entrySet()) {
            Position pos = entry.getKey(); // Текущая позиция сущности
            Entity e = entry.getValue(); // Сама сущность

            // Проверяем, есть ли уже визуальное представление для этой сущности
            Label node = entityNodes.get(e);

            // Если сущность новая (только появилась)
            if (node == null) {
                // Создаем новый Label для этой сущности
                node = createNode(e);
                // Сохраняем соответствие между сущностью и её Label
                entityNodes.put(e, node);
                // Добавляем Label на слой сущностей
                entityLayer.getChildren().add(node);

                // Позиционируем Label по центру клетки
                // pos.getX() * CELL_SIZE - левый край клетки
                // + (CELL_SIZE - node.getWidth()) / 2 - смещение для центрирования
                // Но так как ширина Label еще не вычислена, используем фиксированное смещение
                // Для символа размером 24px смещение примерно 8 пикселей
                node.relocate(pos.getX() * CELL_SIZE + 8, pos.getY() * CELL_SIZE + 8);

                // Анимация появления: плавное увеличение прозрачности от 0 до 1
                node.setOpacity(0); // Начальная прозрачность 0 (невидимый)
                FadeTransition ft = new FadeTransition(Duration.millis(300), node);
                ft.setToValue(1); // Конечная прозрачность 1 (полностью видимый)
                ft.play(); // Запускаем анимацию
            }

            // Проверяем, переместилась ли сущность
            Position old = previousPositions.get(e); // Получаем предыдущую позицию
            if (old != null && !old.equals(pos)) { // Если позиция изменилась
                // Устанавливаем начальную позицию для анимации
                node.relocate(old.getX() * CELL_SIZE + 8, old.getY() * CELL_SIZE + 8);

                // Сбрасываем смещение (translate) на 0
                node.setTranslateX(0);
                node.setTranslateY(0);

                // Создаем анимацию перемещения
                TranslateTransition tt = new TranslateTransition(Duration.millis(300), node);
                
                // Вычисляем смещение для перемещения
                // (pos.getX() - old.getX()) * CELL_SIZE - смещение по X в пикселях
                tt.setToX((pos.getX() - old.getX()) * CELL_SIZE);
                tt.setToY((pos.getY() - old.getY()) * CELL_SIZE);

                // После завершения анимации обновляем реальную позицию Label
                double targetX = pos.getX() * CELL_SIZE + 8;
                double targetY = pos.getY() * CELL_SIZE + 8;
                Label finalNode = node;
                tt.setOnFinished(ev -> {
                    // Сбрасываем смещение
                    finalNode.setTranslateX(0);
                    finalNode.setTranslateY(0);
                    // Устанавливаем новую позицию
                    finalNode.relocate(targetX, targetY);
                });

                // Запускаем анимацию перемещения
                tt.play();

                // Дополнительная анимация: небольшое изменение размера при движении
                ScaleTransition st = new ScaleTransition(Duration.millis(300), node);
                st.setFromX(0.8); // Начальный размер 80%
                st.setFromY(0.8);
                st.setToX(1); // Конечный размер 100%
                st.setToY(1);
                st.play(); // Запускаем анимацию
            }

            // Сохраняем текущую позицию как предыдущую для следующего кадра
            previousPositions.put(e, pos);
        }

        // Удаляем сущности, которые исчезли (умерли)
        // Используем Iterator для безопасного удаления элементов во время итерации
        Iterator<Map.Entry<Entity, Label>> it = entityNodes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Entity, Label> entry = it.next();
            // Если сущность больше не существует в симуляции
            if (!map.containsValue(entry.getKey())) {
                Label node = entry.getValue();

                // Анимация исчезновения: плавное уменьшение прозрачности до 0
                FadeTransition ft = new FadeTransition(Duration.millis(300), node);
                ft.setToValue(0); // Конечная прозрачность 0 (невидимый)

                // Анимация "взрыва": увеличение размера в 2 раза
                ScaleTransition boom = new ScaleTransition(Duration.millis(200), node);
                boom.setToX(2); // Увеличение по X в 2 раза
                boom.setToY(2); // Увеличение по Y в 2 раза

                // После завершения анимации удаляем Label со слоя
                ft.setOnFinished(ev -> entityLayer.getChildren().remove(node));

                // Запускаем обе анимации одновременно
                boom.play();
                ft.play();

                // Удаляем сущность из наших Map
                it.remove();
                previousPositions.remove(entry.getKey());
            }
        }
    }

    // Метод для выполнения одного шага симуляции
    private void nextStep() {
        // Вызываем метод doMove() объекта simulation
        // Этот метод выполняет один цикл симуляции:
        // - все существа делают ход
        // - обновляется состояние мира
        simulation.doMove();
        
        // Обновляем визуализацию с анимацией
        updateGridAnimated();
        
        // Обновляем текст метки с номером цикла
        cycleLabel.setText("Цикл: " + simulation.getCycle());
    }

    // Метод для сброса симуляции (начать заново)
    private void resetSimulation() {
        // Останавливаем таймер
        timer.stop();
        
        // Создаем новый объект симуляции
        simulation = new Simulation(width, height);

        // Очищаем слой сущностей (удаляем все Label)
        entityLayer.getChildren().clear();
        // Очищаем Map с соответствиями сущность-Label
        entityNodes.clear();
        // Очищаем Map с предыдущими позициями
        previousPositions.clear();

        // Обновляем визуализацию (отображаем начальное состояние)
        updateGridAnimated();
        
        // Сбрасываем счетчик циклов
        cycleLabel.setText("Цикл: 0");
    }

    // Главный метод main() - точка входа в программу
    // Вызывается при запуске приложения
    // args - аргументы командной строки (массив строк)
    public static void main(String[] args) {
        // Метод launch() запускает JavaFX приложение
        // Он создает окно (Stage) и вызывает метод start()
        launch(args);
    }
}
