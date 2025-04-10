package faang.school.postservice.config.moderation;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для настроек модерации постов.
 * <p>
 * Содержит параметры, необходимые для работы системы модерации контента.
 * Значения параметров загружаются из файлов конфигурации приложения.
 *
 * <h3>Содержащиеся параметры:</h3>
 * <ul>
 *   <li><b>Путь к словарю</b> - местоположение файла с запрещенными словами</li>
 *   <li><b>Размер пакета</b> - количество постов, обрабатываемых за одну операцию</li>
 * </ul>
 *
 */
@Getter
@Configuration
public class ModerationConfig {

    @Value("${moderation.dictionary.path}")
    private String dictionaryPath;

    @Value("${moderation.dictionary.batch.size}")
    private int batchSize;
}