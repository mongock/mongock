package io.mongock.driver.core.interceptor;


import io.mongock.driver.api.lock.LockManager;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvokerImpl;
import io.mongock.driver.api.lock.guard.invoker.VoidSupplier;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import java.util.function.Supplier;


public class LockMethodInvokerTest {


  @Test
  public void shouldEnsureLock_WhenNormalSupplier_IfInvoke() {
    // given
    LockManager lockManagerMock = Mockito.mock(LockManager.class);
    Supplier supplierMock = Mockito.mock(Supplier.class);

    // when
    new LockGuardInvokerImpl(lockManagerMock).invoke(supplierMock);

    //then
    Mockito.verify(lockManagerMock, new Times(1)).ensureLockDefault();
    Mockito.verify(supplierMock, new Times(1)).get();

  }

  @Test
  public void shouldEnsureLock_WhenVoidSupplier_IfInvoke() {
    // given
    LockManager lockManagerMock = Mockito.mock(LockManager.class);
    VoidSupplier supplierMock = Mockito.mock(VoidSupplier.class);

    // when
    new LockGuardInvokerImpl(lockManagerMock).invoke(supplierMock);

    //then
    Mockito.verify(lockManagerMock, new Times(1)).ensureLockDefault();
    Mockito.verify(supplierMock, new Times(1)).execute();

  }

}
