package com.yoursway.model.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import com.yoursway.model.timeline.PointInTime;
import com.yoursway.model.timeline.Timeline;
import com.yoursway.utils.collections.HashSetMultiMap;
import com.yoursway.utils.collections.MultiMap;

public class Scheduler implements IRepository, ConsumerTrackerMaster, ModelTrackerMaster,
        CalculatedModelTrackerMaster {
    
    private final ExecutorService executorService;
    private final Timeline timeline;
    
    private final Collection<ConsumerTracker> consumers = new ArrayList<ConsumerTracker>();
    
    private final Map<Class<?>, CalculatedModelTracker> calculatedModels = new HashMap<Class<?>, CalculatedModelTracker>();
    private final Map<Class<?>, BasicModelTracker> basicModels = new HashMap<Class<?>, BasicModelTracker>();
    
    private final MultiMap<IHandle<?>, IDependant> dependencies = new HashSetMultiMap<IHandle<?>, IDependant>(); // would it better to store them for each model separately
    private final SimpleSnapshotStorage snapshotStorage;
    
    public Scheduler(Timeline timeline, ExecutorService executorService) {
        this.timeline = timeline;
        this.executorService = executorService;
        this.snapshotStorage = new SimpleSnapshotStorage();
    }
    
    public <T> IBasicModelChangesRequestor addBasicModel(Class<T> rootHandleInterface, T rootHandle) {
        BasicModelTracker tracker = new BasicModelTracker(rootHandleInterface, rootHandle, this,
                executorService);
        basicModels.put(rootHandleInterface, tracker);
        return tracker;
    }
    
    public void addConsumer(IConsumer consumer) {
        ConsumerTracker consumerTracker = new ConsumerTracker(consumer, this);
        consumers.add(consumerTracker);
        consumerTracker.call(snapshotStorage, timeline.now(), null); //delta = null?
    }
    
    public <T> void registerModel(Class<T> rootHandleInterface, T rootHandle,
            ICalculatedModelUpdater modelUpdater) {
        CalculatedModelTracker tracker = new CalculatedModelTracker(rootHandleInterface, rootHandle, this,
                modelUpdater, executorService);
        calculatedModels.put(rootHandleInterface, tracker);
        tracker.call(snapshotStorage, timeline.now(), null);
    }
    
    @SuppressWarnings("unchecked")
    public <V extends IModelRoot> V obtainRoot(Class<V> rootInterface) {
        BasicModelTracker tracker = basicModels.get(rootInterface);
        V result = (V) tracker.getRootHandle();
        if (result != null)
            return result;
        CalculatedModelTracker tracker2 = calculatedModels.get(rootInterface);
        result = (V) tracker2.getRootHandle();
        if (result != null)
            return result;
        throw new AssertionError("No model provides a root of type " + rootInterface);
    }
    
    public void addDependency(IDependant tracker, IHandle<?> handle) {
        dependencies.put(handle, tracker);
    }
    
    public void handlesChanged(PointInTime moment, ModelDelta delta) {
        // Update consumers
        Set<IDependant> trackersToUpdate = new HashSet<IDependant>();
        for (IHandle<?> handle : delta.getChangedHandles())
            trackersToUpdate.addAll(dependencies.get(handle));
        update(moment, trackersToUpdate, delta);
    }
    
    private void update(PointInTime moment, Set<IDependant> trackersToUpdate, ModelDelta delta) {
        for (IDependant tracker : trackersToUpdate) {
            tracker.call(snapshotStorage, moment, delta);
        }
    }
    
    public PointInTime createPointInTime() {
        return timeline.advanceThisCrazyWorldToTheNextMomentInTime();
    }
    
    public ISnapshotStorage getSnapshotStorage() {
        return snapshotStorage;
    }
    
}
