package home.hyywk.top.scheduling.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Component
public class SchedulingTask {

    private Logger logger = LoggerFactory.getLogger( SchedulingTask.class );
    private InputStream inputStream;
    private Process process;

    /**
     * 启动执行top命令,必须带上-b选项,当top向其它程序进行输出的时候,需要带上-b选项,否则无法得到想要的信息.
     */
    public SchedulingTask() {
        try {
            this.logger.info( "启动top任务" );
            this.process = Runtime.getRuntime().exec( "top -b ");
            this.inputStream = this.process.getInputStream();
            this.readTopInfo( this.inputStream );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置定时任务
     */
    @Scheduled( fixedRate =  5000 )
    public void reportStatus() {
        this.logger.info( "当前时间:{}", new Date() );
        try {
            if ( process.isAlive() ) {
                this.logger.info( "运行正常!" );
                this.readTopInfo( this.inputStream );
            } else {
                this.logger.info( "停止运行!" );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取top命令输出的信息
     * @param inputStream
     * @throws IOException
     */
    public void readTopInfo( InputStream inputStream ) throws IOException {
        if ( inputStream.available() > 0)  {
            byte[] bs = new byte[2048];
            inputStream.read( bs );
            System.out.println( new String( bs ) );
        }
    }

    /**
     * 关闭流
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            this.inputStream.close();
        } catch (IOException e) {
            this.logger.error( "io流关闭失败!" );
            e.printStackTrace();
        }
    }
}
