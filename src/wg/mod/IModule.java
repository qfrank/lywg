package wg.mod;



public interface IModule {

	/**
	 * Can be called only once for one module instance.<br/>
	 * For IExclusiveModule,when override this method,event like SCENE_INITED should not be processed in this method!
	 */
	void init();

	void run();

	/**
	 * If stop successfully return true else false
	 * @return
	 */
	boolean stop();

	/**
	 * Can be called only once for one module instance
	 */
	void destroy();
	
}
