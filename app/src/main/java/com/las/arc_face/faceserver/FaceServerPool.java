package com.las.arc_face.faceserver;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class FaceServerPool {

    private static final GenericObjectPool<FaceServer> video = new GenericObjectPool(new ObjectFactory());

    public static class ObjectFactory extends BasePooledObjectFactory<FaceServer> {

        @Override
        public FaceServer create() throws Exception {
            return new FaceServer();
        }

        @Override
        public PooledObject wrap(FaceServer obj) {
            return new DefaultPooledObject(obj);
        }

        @Override
        public void destroyObject(PooledObject<FaceServer> p) throws Exception {
            FaceServer faceServer = p.getObject();
            faceServer.unInit();
            super.destroyObject(p);
        }
    }


}
