package com.mydice.main;

import kotlin.Unit;
import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.Job;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactoryJvm;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.Events;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.BotConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.mydice.tool.rd.dicecommand;
import static com.mydice.tool.rd.r;

class BlockingTest {

    public static void main(String[] args) throws InterruptedException {
        // 使用自定义的配置
        final Bot bot = BotFactoryJvm.newBot(123, "", new BotConfiguration() {
            {

                //保存设备信息到文件
                fileBasedDeviceInfo("deviceInfo.json");
                // setLoginSolver();
                // setBotLoggerSupplier();
            }
        });


        // 使用默认的配置
        // BlockingBot bot = BlockingBot.newInstance(123456, "");

        bot.login();

        //输出好友
        bot.getFriends().forEach(friend -> System.out.println(friend.getId() + ":" + friend.getNick()));
        Events.registerEvents(bot, new SimpleListenerHost() {
            //EventHandler可以指定多个属性，包括处理方式、优先级、是否忽略已取消的事件
            //其默认值请见EventHandler注解类
            //因为默认处理的类型为Listener.ConcurrencyKind.CONCURRENT
            //需要考虑并发安全
            @EventHandler
            public ListeningStatus onGroupMessage(GroupMessageEvent event) {
                String msgString = BlockingTest.toString(event.getMessage());
                if (msgString.contains("reply")) {
                    // 引用回复
                    final QuoteReply quote = new QuoteReply(event.getSource());
                    event.getGroup().sendMessage(quote.plus("引用回复"));

                } else if (msgString.contains("at")) {
                    // at
                    event.getGroup().sendMessage(new At(event.getSender()));

                } else if (msgString.contains("permission")) {
                    // 成员权限
                    event.getGroup().sendMessage(event.getPermission().toString());

                } else if (msgString.contains("mixed")) {
                    // 复合消息, 通过 .plus 连接两个消息
                    event.getGroup().sendMessage(
                            MessageUtils.newImage("{01E9451B-70ED-EAE3-B37C-101F1EEBF5B5}.png") // 演示图片, 可能已过期
                                    .plus("Hello") // 文本消息
                                    .plus(new At(event.getSender())) // at 群成员
                                    .plus(AtAll.INSTANCE) // at 全体成员
                    );
                } else if (msgString.contains("recall1")) {
                    event.getGroup().sendMessage("你看不到这条消息").recall();
                    // 发送消息马上就撤回. 因速度太快, 客户端将看不到这个消息.

                } else if (msgString.contains("recall2")) {
                    final Job job = event.getGroup().sendMessage("3秒后撤回").recallIn(3000);

                    job.cancel(new CancellationException()); // 可取消这个任务
                    String groupid=""+event.getGroup().getId();
                    event.getGroup().sendMessage(groupid);
                } else if (msgString.contains("上传图片")) {
                    File file = new File("hello.jpg");
                    if (file.exists()) {
                        final Image image = event.getGroup().uploadImage(new File("hello.jpg"));
                        // 上传一个图片并得到 Image 类型的 Message

                        final String imageId = image.getImageId(); // 可以拿到 ID
                        final Image fromId = MessageUtils.newImage(imageId); // ID 转换得到 Image
                        event.getGroup().sendMessage(image); // 发送图片
                    }

                } else if (msgString.contains("friend")) {
                    final Future<MessageReceipt<Contact>> future = event.getSender().sendMessageAsync("Async send"); // 异步发送
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    //将图片转换为图片ID
                } else if (msgString.startsWith("convert")) {
                    StringBuilder stringBuilder = new StringBuilder("结果：\n");
                    event.getMessage().forEachContent(msg ->
                            {
                                if (msg instanceof Image) {
                                    stringBuilder.append(((Image) msg).getImageId());
                                    stringBuilder.append("\n");
                                }
                                return Unit.INSTANCE;// kotlin 的所有函数都有返回值. Unit 为最基本的返回值. 请在这里永远返回 Unit
                            }
                    );
                    event.getGroup().sendMessage(stringBuilder.toString());
                } else if (msgString.equals("muteAll")) {
                    //全体禁言
                    event.getGroup().getSettings().setMuteAll(true);
                } else if (msgString.startsWith(".")||msgString.startsWith("。")){
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(dicecommand(msgString));
                    event.getGroup().sendMessage(stringBuilder.toString());
                } else if (msgString.contains("[图片]")||!msgString.startsWith("convert")){
                    event.getMessage().forEachContent(msg ->
                            {
                                if (msg instanceof Image) {
                                    String imageId= ((Image) msg).getImageId();
                                    if(imageId.equals("{398A45CE-784C-9555-DD86-41CAF1F60F15}.mirai")){
                                        final Image fromId = MessageUtils.newImage("{B1224062-5361-297C-8764-3B0CFDDEEC71}.mirai"); // ID 转换得到 Image
                                        event.getGroup().sendMessage(fromId); // 发送图片
                                    }
                                }
                                return Unit.INSTANCE;// kotlin 的所有函数都有返回值. Unit 为最基本的返回值. 请在这里永远返回 Unit
                            }
                    );
                }
                //保持监听
                return ListeningStatus.LISTENING;
            }


            @EventHandler
            public ListeningStatus onFriendMessage(FriendMessageEvent event) {
                String msgString = BlockingTest.toString(event.getMessage());
                if (msgString.contains("reply")) {
                    // 引用回复
                    final QuoteReply quote = new QuoteReply(event.getSource());
                    event.getSender().sendMessage(quote.plus("引用回复"));

                } else if (msgString.contains("recall1")) {
                    event.getSender().sendMessage("你看不到这条消息").recall();
                    // 发送消息马上就撤回. 因速度太快, 客户端将看不到这个消息.

                } else if (msgString.contains("recall2")) {
                    final Job job = event.getSender().sendMessage("3秒后撤回").recallIn(3000);

                    job.cancel(new CancellationException()); // 可取消这个任务
                } else if (msgString.contains("上传图片")) {
                    File file = new File("hello.jpg");
                    if (file.exists()) {
                        final Image image = event.getSender().uploadImage(new File("hello.jpg"));
                        // 上传一个图片并得到 Image 类型的 Message

                        final String imageId = image.getImageId(); // 可以拿到 ID
                        final Image fromId = MessageUtils.newImage(imageId); // ID 转换得到 Image

                        event.getSender().sendMessage(image); // 发送图片
                    }

                } else if (msgString.contains("friend")) {
                    final Future<MessageReceipt<Contact>> future = event.getSender().sendMessageAsync("Async send"); // 异步发送
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    //将图片转换为图片ID
                } else if (msgString.startsWith("convert")) {
                    StringBuilder stringBuilder = new StringBuilder("结果：\n");
                    event.getMessage().forEachContent(msg ->
                            {
                                if (msg instanceof Image) {
                                    stringBuilder.append(((Image) msg).getImageId());
                                    stringBuilder.append("\n");
                                }
                                return Unit.INSTANCE;// kotlin 的所有函数都有返回值. Unit 为最基本的返回值. 请在这里永远返回 Unit
                            }
                    );
                    event.getSender().sendMessage(stringBuilder.toString());
                } else if (msgString.startsWith(".")||msgString.startsWith("。")){
                    event.getSender().sendMessage(msgString);
                } else if (msgString.contains("[图片]")||!msgString.startsWith("convert")){
                    event.getMessage().forEachContent(msg ->
                            {
                                if (msg instanceof Image) {
                                    String imageId= ((Image) msg).getImageId();
                                    if(imageId.equals("{398A45CE-784C-9555-DD86-41CAF1F60F15}.mirai")){
                                        final Image fromId = MessageUtils.newImage("{B1224062-5361-297C-8764-3B0CFDDEEC71}.mirai"); // ID 转换得到 Image
                                        event.getSender().sendMessage(fromId); // 发送图片
                                    }
                                }
                                return Unit.INSTANCE;// kotlin 的所有函数都有返回值. Unit 为最基本的返回值. 请在这里永远返回 Unit
                            }
                    );
                }
                //保持监听
                return ListeningStatus.LISTENING;
            }


            //处理在处理事件中发生的未捕获异常
            @Override
            public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
                throw new RuntimeException("在事件处理中发生异常", exception);
            }
        });

        bot.join(); // 阻塞当前线程直到 bot 离线
    }

    private static String toString(MessageChain chain) {
        return chain.contentToString();
    }
}