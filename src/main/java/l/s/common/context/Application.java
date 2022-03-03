package l.s.common.context;

public class Application {

    private static Context context;

    public static Context getContext() {
        if(context == null){
            context = GlobalContext.getContext();
        }
        return context;
    }

    public static void setContext(Context context) {
        Application.context = context;
    }

}
