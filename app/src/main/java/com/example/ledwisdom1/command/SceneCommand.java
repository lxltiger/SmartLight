package com.example.ledwisdom1.command;

public abstract class SceneCommand extends MeshStatus{

   private int sceneAddress;
   private int dstAddress;


    public SceneCommand() {
    }

    public SceneCommand(int sceneAddress, int dstAddress) {
        this.sceneAddress = sceneAddress;
        this.dstAddress = dstAddress;
    }

    public void setDstAddress(int dstAddress) {
        this.dstAddress = dstAddress;
    }

    public void setSceneAddress(int sceneAddress) {
        this.sceneAddress = sceneAddress;
    }

    public final void handleSceneOperation(SceneOperation operation, /*int sceneAddress, int dstAddress,*/ byte light, byte red, byte green, byte blue) {
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
