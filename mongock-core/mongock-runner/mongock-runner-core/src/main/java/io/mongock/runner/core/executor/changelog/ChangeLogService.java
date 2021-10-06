package io.mongock.runner.core.executor.changelog;

import io.mongock.api.ChangeLogItem;
import io.mongock.api.ChangeSetItem;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.exception.MongockException;
import io.mongock.runner.core.annotation.AnnotationProcessor;
import io.mongock.runner.core.annotation.LegacyAnnotationProcessor;
import io.mongock.runner.core.annotation.LegacyLegacyAnnotationProcessor;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


/**
 * Utilities to deal with reflections and annotations
 *
 * @since 27/07/2014
 */
public class ChangeLogService extends ChangeLogServiceBase<ChangeLogItem<ChangeSetItem>, ChangeSetItem> {


  private static final LegacyLegacyAnnotationProcessor LEGACY_ANNOTATION_PROCESSOR = new LegacyLegacyAnnotationProcessor();
  private static final AnnotationProcessor DEFAULT_ANNOTATION_PROCESSOR = new AnnotationProcessor();


  /**
   * @param changeLogsBasePackageList   list of changeLog packages
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
  protected ChangeLogItem<ChangeSetItem> buildChangeLogInstance(Class<?> changeUnitClass) throws MongockException {
    ChangeUnit changeUnit = changeUnitClass.getAnnotation(ChangeUnit.class);
    AnnotationProcessor annotationProcessor = getAnnotationProcessor();
    annotationProcessor.validateChangeUnit(changeUnitClass);

    return ChangeLogItem.getFromAnnotation(
        changeUnitClass,
        changeUnit.id(),
        changeUnit.author(),
        changeUnit.order(),
        changeUnit.failFast(),
        changeUnit.runAlways(),
        changeUnit.systemVersion(),
        annotationProcessor.getExecuteMethod(changeUnitClass),
        annotationProcessor.getRollbackMethod(changeUnitClass),
        annotationProcessor.getBeforeMethod(changeUnitClass).orElse(null),
        annotationProcessor.getRollbackBeforeMethod(changeUnitClass).orElse(null)

    );
  }

  @Override
  protected ChangeLogItem<ChangeSetItem> buildChangeLogInstanceFromLegacy(Class<?> changeLogClass) {
    LegacyAnnotationProcessor<ChangeSetItem> annProcessor = getLegacyAnnotationProcessor();
    return  ChangeLogItem.getFromLegacy(
        changeLogClass,
        annProcessor.getChangeLogOrder(changeLogClass),
        annProcessor.isFailFast(changeLogClass),
        fetchListOfChangeSetsFromClass(changeLogClass));
  }

}
