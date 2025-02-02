package dataStorage;

public class Constants {
    public static final String MANUAL =
            """
                Руководство пользователя.\n
                1. Ввод задачи.
                    1) Для ручного ввода задачи требуется перейти во вкладку Задача, далее, следуя названиям полей заполнить их.
                    В случае, если будут введены не корректные данные, приложение выдаст сообщение об ошибке. После ввода параметров задачи
                    в левом меню следует нажать кнопку Применить, после чего будет сгенерирована таблица с нужным количеством полей, которые также следует заполнить.
                    После полного ввода задачи нажмите кнопку считать задачу (это важно для того, чтобы ее можно было сохранить позже см. Сохранение задачи).
                    Как только задача будет считана выделится одна из двух кнопок - симплекс метод или метод искусственного базиса, в зависимости от выбранного режима.
                    Нажимая на появившуюся кнопку вы попадете в соответствующую вкладку.
                    2) Для ввода задачи из файла требуется нажать на кнопку файл в верхнем левом углу и выбрать загрузить задачу из файла.
                    Далее откроется проводник и будет возможность выбрать нужный файл. Файл представляет из себя файл с расширением .json,
                    подробнее о структуре файла можно узнать в документации приложения.
                    Как только вы выберете нужный файл задача сразу откроется в нужной вкладке (см. Вкладки) а данные задачи сразу будут преобразованы к нужному формату.
                2. Сохранение задачи.
                    Для сохранения задачи требуется в ранее описанном меню Файл выбрать пункт сохранить задачу.
                    Далее откроется проводник и вы сможете выбрать либо уже существующий файл (Внимание! при выборе уже существующего файла данные будут перезаписаны!),
                    либо написать название нового файла и он создастся автоматически. Файл также должен иметь расширение .json.
                    Сохранять задачу можно в любой момент решения после того, как будет нажата кнопка Считать задачу, либо как только она будет считана из файла.
                3. Вкладки.
                    1) Метод искусственного базиса. В этой вкладке происходит поиск начального решения задачи.
                    В нее можно попасть, если в введенной задаче был вабран искусственный базис.
                    Решение может быть как автоматическим, так и пошаговым (см. Режимы), в зависимости от того, что указано в задаче.
                    Как только решение метода закончится, будет предложено перейти к симплекс методу, либо сделать шаг назад (если выбран пошаговый режим).
                    2) Симплекс метод. В этой вкладке реализуется решение исходной задачи путем использования симплекс-метода.
                    В этот раздел можно попасть как сразу после ввода задачи, если был дан готовый базис, так и после метода искусственного базиса.
                    В конце этого режима вам будет представлено решение задачи в виде: вектор аргументов и оптимальное значение функции.
                4. Режимы.
                    1) Автоматичсеский режим. В этом режиме нет возможности просмотреть каждый шаг решения.
                    В случае использования метода искусственного базиса, сначала будет возможность увидеть итоговое решение этого метода, а позже перейти в симплекс метод
                    и увидеть итоговое решение задачи.
                    2) Пошаговый режим. В этом режиме есть возможность просматривать каждый промежуточный шаг решения.
                    В каждом шаге требуется также выбирать опорный элемент (возможные варианты подсвечены желтым цветом).
                    Помимо шага вперед, есть возможность сделать шаг назад, тогда будет показана предыдущая симплекс-таблица.
                    Во время шагов назад будет автоматически произведен переход на метод искусственного базиса, если потребуется.
                5. Предупреждения и сообщения об ошибках.
                    В случае некорректных данных, либо иных неправильных действий приложение выдаст сообщение об ошибке, всегда рекомендуется ознакамливаться с содержимым сообщения.
            """;
}
