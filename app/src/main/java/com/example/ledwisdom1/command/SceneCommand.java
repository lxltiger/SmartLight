package com.example.ledwisdom1.command;

public abstract class SceneCommand extends MeshStatus{



    public SceneCommand() {

    }



    //    带延迟的开关灯 delay一般是5秒，0就是立马执行
    public final void handleSceneOperation(SceneOperation operation,int sceneAddress,int dstAddress, byte light,  byte red, byte green, byte blue) {
        if (isBlueTooth()) {
            if (isStatusValid()) {
                handleSceneOperationByBLE(operation,sceneAddress,dstAddress,light, red, green, blue);
            }
        } else {
            handleSceneOperationByWIFI(operation,sceneAddress,dstAddress,light, red, green, blue);
        }
    }



    protected abstract void handleSceneOperationByBLE(SceneOperation sceneOperation, int sceneAddress, int dstAddress, byte light, byte red, byte green, byte blue);

    //wifi没有发现delay方法 先加上
    protected abstract void handleSceneOperationByWIFI(SceneOperation sceneOperation, int sceneAddress, int dstAddress, byte light, byte red, byte green, byte blue);

    public enum SceneOperation {
        ADD, DELETE, LOAD
    }
}
