package io.mongock.runner.core.executor.changelog;

import io.mongock.driver.api.common.SystemChange;
import io.mongock.runner.core.internal.ChangeLogItem;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.exception.MongockException;
import io.mongock.runner.core.annotation.AnnotationProcessor;
import io.mongock.runner.core.annotation.LegacyAnnotationProcessor;
import io.mongock.runner.core.annotation.LegacyLegacyAnnotationProcessor;
import io.mongock.utils.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


/**
 * Utilities to deal with reflections and annotations
 *
 * @since 27/07/2014
 */
public class ChangeLogService extends ChangeLogServiceBase {


  private static final LegacyLegacyAnnotationProcessor LEGACY_ANNOTATION_PROCESSOR = new LegacyLegacyAnnotationProcessor();
  private static final AnnotationProcessor DEFAULT_ANNOTATION_PROCESSOR = new AnnotationProcessor();


  /**
   * @param changeLogsBasePackageList   list of changeLog packages
   * @param changeLogsBaseClassList     list of changeLog classes
   * @param startSystemVersionInclusive inclusive starting systemVersion
   * @param endSystemVersionInclusive   inclusive ending systemVersion
   */
  public ChangeLogService(List<String> changeLogsBasePackageList,
                          List<Class<?>> changeLogsBaseClassList,
                          String startSystemVersionInclusive,
                          String endSystemVersionInclusive) {
    this(changeLogsBasePackageList, changeLogsBaseClassList, startSystemVersionInclusive, endSystemVersionInclusive, null);
  }



  public ChangeLogService(List<String> changeLogsBasePackageList,
                          List<Class<?>> changeLogsBaseClassList,
                          String startSystemVersionInclusive,
                          String endSystemVersionInclusive,
                          Function<AnnotatedElement, Boolean> profileFilter) {
    this();
    setChangeLogsBasePackageList(new ArrayList<>(changeLogsBasePackageList));
    setChangeLogsBaseClassList(changeLogsBaseClassList);
    setStartSystemVersion(startSystemVersionInclusive);
    setEndSystemVersion(endSystemVersionInclusive);
    setProfileFilter(profileFilter);
  }

  public ChangeLogService() {
    super(DEFAULT_ANNOTATION_PROCESSOR, LEGACY_ANNOTATION_PROCESSOR);
  }


  @Override
  protected ChangeLogItem buildChangeLogInstance(Class<?> changeUnitClass) throws MongockException {
    ChangeUnit changeUnit = changeUnitClass.getAnnotation(ChangeUnit.class);
    AnnotationProcessor annotationProcessor = getAnnotationProcessor();
    annotationProcessor.validateChangeUnit(changeUnitClass);

    return ChangeLogItem.getFromAnnotation(
        changeUnitClass,
        changeUnit.id(),
        StringUtils.hasText(changeUnit.author()) ? changeUnit.author() : getDefaultMigrationAuthor(),
        changeUnit.order(),
        changeUnit.failFast(),
        changeUnit.transactional(),
        changeUnit.runAlways(),
        changeUnit.systemVersion(),
        annotationProcessor.getExecuteMethod(changeUnitClass),
        annotationProcessor.getRollbackMethod(changeUnitClass),
        annotationProcessor.getBeforeMethod(changeUnitClass).orElse(null),
        annotationProcessor.getRollbackBeforeMethod(changeUnitClass).orElse(null),
        changeUnitClass.isAnnotationPresent(SystemChange.class)

    );
  }

  @Override
  protected ChangeLogItem buildChangeLogInstanceFromLegacy(Class<?> changeLogClass) {
    LegacyAnnotationProcessor annProcessor = getLegacyAnnotationProcessor();
    return  ChangeLogItem.getFromLegacy(
        changeLogClass,
        annProcessor.getChangeLogOrder(changeLogClass),
        annProcessor.isFailFast(changeLogClass),
        fetchListOfChangeSetsFromClass(changeLogClass),
        changeLogClass.isAnnotationPresent(SystemChange.class));
  }

}
