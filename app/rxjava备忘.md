Subscribe  订阅

    Observable 和 Observer 之后，再用 subscribe() 方法将它们联结起来

Flowable 解决背压问题


Consumer

> 简化订阅方法，我们可以根据需求，选择相应的简化订阅。只不过传入的对象改为了Consumer。

       Disposable disposable = observable.subscribe(new Consumer<Integer>() {
    @Override
    public void accept(Integer integer) throws Exception {
      //这里接收数据项
    }
    }, new Consumer<Throwable>() {
    @Override
    public void accept(Throwable throwable) throws Exception {
      //这里接收onError
    }
    }, new Action() {
    @Override
    public void run() throws Exception {
      //这里接收onComplete。
    }
    });