package aixxe;

import net.twasi.obsremotejava.OBSRemoteController;

public class StreamController extends Thread {
    private boolean running = true;
    private boolean connected = false;
    private OBSRemoteController controller = null;
    private String hostAddress = "localhost";
    private String hostPort = "4444";
    public SceneCollection scenes = new SceneCollection();

    /**
     * Set the address and port we should connect to.
     *
     * @param hostAddress Remote OBS WebSocket server address.
     * @param hostPort Remote OBS WebSocket server port.
     */
    public void setHostDetails(String hostAddress, String hostPort) {
        this.hostAddress = hostAddress;
        this.hostPort = hostPort;
    }

    /**
     * Connect to the defined OBS WebSocket server and manage internal state.
     */
    private void connect() {
        controller = new OBSRemoteController("ws://" + hostAddress + ":" + hostPort, false);
        controller.registerConnectCallback(response -> connected = true);
        controller.registerDisconnectCallback(response -> connected = false);
    }

    /**
     * Thread to handle automatic reconnecting when a connection is lost.
     */
    public void run() {
        while (running) {
            if (!connected) {
                connect();
            }

            try {
                sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /**
     * Signal the connection thread to terminate.
     */
    public void shutdown() {
        running = false;
    }

    /**
     * Change the current scene in OBS.
     *
     * @param scene The new scene to switch to.
     */
    public void switchScene(String scene) {
        if (connected) {
            controller.setCurrentScene(scene, response -> {});
        }
    }
}
