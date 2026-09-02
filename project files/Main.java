package org.example;
import java.io.File;
import org.orekit.data.DataContext;
import org.orekit.data.DataProvider;
import org.orekit.data.DirectoryCrawler;

public class Main {
    public static void main(String[] args) {
        final File orekitData = new File("file path");
        final DataProvider dirCrawler = new DirectoryCrawler(orekitData);
        DataContext.getDefault().getDataProvidersManager().addProvider(dirCrawler);

        maneuevers.maneuevers(args);
        keplerorbit.keplerorbit(args);
//        attitude.attitude(args);
    }
}
