package com.cp2y.cube.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * Created by yangfan on 2017/9/19.
 * 保存号码格式
 */
public class LotterySaveUtils {

    /**
     * 双色球大乐透单式保存格式
     * @param file
     * @param data
     */
    public static void saveSelectNum(File file, List<String> data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (int j = 0; j < data.size(); j++) {
                if (j == data.size()-1) {
                    writer.write(CommonUtil.preZeroForBall(data.get(j)));
                } else {
                    writer.write(CommonUtil.preZeroForBall(data.get(j)) + " ");
                }
                //writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 双色球大乐透复式保存格式
     * @param file
     * @param data
     */
    public static void saveSelectDoubleNum(File file, List<String> data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            int red = 0;
            int blue = 0;
            for (int i = 0; i < data.size(); i++) {
                if (Integer.parseInt(data.get(i)) < 50) {
                    red++;
                } else {
                    blue++;
                }
            }
            for (int j = 0; j < data.size(); j++) {
                if (j < red) {
                    if (j == red - 1) {
                        writer.write(CommonUtil.preZeroForBall(data.get(j)));
                    } else {
                        writer.write(CommonUtil.preZeroForBall(data.get(j)) + " ");
                    }
                } else {
                    if (j == red) {
                        writer.write("#" + CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 50)) + " ");
                    } else if (j == data.size() - 1) {
                        writer.write(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 50)));
                    } else {
                        writer.write(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 50)) + " ");
                    }

                }
                //writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 双色球胆拖保存格式
     * @param file
     * @param data
     */
    public static void saveSelectDanTuoNum(File file, List<String> data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            int red_dan = 0;
            int red_tuo = 0;
            int blue = 0;
            for (int i = 0; i < data.size(); i++) {
                if (Integer.parseInt(data.get(i)) < 50) {
                    red_dan++;
                } else if (Integer.parseInt(data.get(i)) > 50 && Integer.parseInt(data.get(i)) < 100) {
                    red_tuo++;
                } else {
                    blue++;
                }
            }
            for (int j = 0; j < data.size(); j++) {
                if (j < red_dan) {
                    if (j == red_dan - 1) {
                        writer.write(CommonUtil.preZeroForBall(data.get(j)));
                    } else {
                        writer.write(CommonUtil.preZeroForBall(data.get(j)) + " ");
                    }
                } else if (j >= red_dan && j < red_tuo + red_dan) {
                    if (j == red_dan) {
                        writer.write("#" + CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 50)) + " ");
                    } else if (j == (red_tuo + red_dan - 1)) {
                        writer.write(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 50)) + "");
                    } else {
                        writer.write(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 50)) + " ");
                    }

                } else {
                    if (j == red_tuo + red_dan) {
                        writer.write("#" + CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 100)) + " ");
                    } else if (j == data.size() - 1) {
                        writer.write(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 100)) + "");
                    } else {
                        writer.write(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 100)) + " ");
                    }

                }

                //writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 大乐透胆拖保存格式
     * @param file
     * @param data
     */
    public static void saveLottoDanTuoNum(File file, List<String> data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            int red_dan = 0;
            int red_tuo = 0;
            int blue_dan = 0;
            int blue_tuo = 0;
            for (int i = 0; i < data.size(); i++) {
                if (Integer.parseInt(data.get(i)) < 50) {
                    red_dan++;
                } else if (Integer.parseInt(data.get(i)) > 50 && Integer.parseInt(data.get(i)) < 100) {
                    red_tuo++;
                } else if (Integer.parseInt(data.get(i)) > 100 && Integer.parseInt(data.get(i)) < 150) {
                    blue_dan++;
                } else {
                    blue_tuo++;
                }
            }
            for (int j = 0; j < data.size(); j++) {
                if (j < red_dan) {//红胆
                    if (j == red_dan - 1) {
                        writer.write(CommonUtil.preZeroForBall(data.get(j)));
                    } else {
                        writer.write(CommonUtil.preZeroForBall(data.get(j)) + " ");
                    }
                } else if (j >= red_dan && j < red_tuo + red_dan) {//红拖
                    if (j == red_dan) {
                        writer.write("#" + CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 50)) + " ");
                    } else if (j == (red_tuo + red_dan - 1)) {
                        writer.write(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 50)) + "");
                    } else {
                        writer.write(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 50)) + " ");
                    }

                } else if (j >= red_tuo + red_dan && blue_dan == 0) {//蓝胆
                    if (j == red_tuo + red_dan) {//前胆为空
                        writer.write("# #" + CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 150)) + " ");
                    } else if (j == data.size() - 1) {
                        writer.write(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 150)) + "");
                    } else {
                        writer.write(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 150)) + " ");
                    }
                } else if (j >= red_tuo + red_dan && blue_dan != 0) {
                    if (blue_dan == 1) {//蓝胆一个没空格
                        if (j == red_tuo + red_dan) {
                            writer.write("#" + CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 100)) + "");
                        } else if (j >= red_tuo + red_dan + blue_dan) {
                            if (j == red_tuo + red_dan + blue_dan && j == data.size() - 1) {
                                writer.write("#" + CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 150)) + "");
                            } else if (j == red_tuo + red_dan + blue_dan) {
                                writer.write("#" + CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 150)) + " ");
                            } else if (j == data.size() - 1) {
                                writer.write(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 150)) + "");
                            } else {
                                writer.write(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 150)) + " ");
                            }
                        }
                    } else {//蓝拖
                        if (j >= red_tuo + red_dan && j < red_tuo + red_dan + blue_dan) {
                            if (j == red_tuo + red_dan) {
                                writer.write("#" + CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 100)) + " ");
                            } else if (j == red_tuo + red_dan + blue_dan - 1) {
                                writer.write(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 100)) + "");
                            } else {
                                writer.write(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 100)) + " ");
                            }
                            writer.write(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 100)) + "");
                        } else if (j >= red_tuo + red_dan + blue_dan) {
                            if (j == red_tuo + red_dan + blue_dan) {
                                writer.write("#" + CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 150)) + " ");
                            } else if (j == data.size() - 1) {
                                writer.write(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 150)) + "");
                            } else {
                                writer.write(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 150)) + " ");
                            }
                        }
                    }
                }
                //writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 福彩3D单式保存格式
     * @param file
     * @param data
     */
    public static void save3dNum(File file, List<String> data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (int j = 0; j < data.size(); j++) {
                writer.write(data.get(j));
                //writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 福彩3D直选定位保存格式
     * @param file
     * @param data
     */
    public static void saveD3LocationNum(File file, List<String> data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            int bai = 0;
            int shi = 0;
            for (int i = 0; i < data.size(); i++) {
                bai=(Integer.parseInt(data.get(i))  >= 50&&bai==0)?i:bai;
                shi=(Integer.parseInt(data.get(i))  >= 100&&shi==0)?i:shi;
            }
            for (int j = 0; j < data.size(); j++) {
                if (j < bai) {
                    writer.write(data.get(j));
                } else if (j >= bai && j < shi) {
                    if (j == bai) {
                        writer.write("-" + String.valueOf(Integer.parseInt(data.get(j)) - 50));
                    } else {
                        writer.write(String.valueOf(Integer.parseInt(data.get(j)) - 50));
                    }
                } else {
                    if (j == shi) {
                        writer.write("-" + String.valueOf(Integer.parseInt(data.get(j)) - 100));
                    } else {
                        writer.write(String.valueOf(Integer.parseInt(data.get(j)) - 100));
                    }
                }
                //writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 重庆时时彩2星直选定位保存格式
     * @param file
     * @param data
     */
    public static void saveCQ2LocationNum(File file, List<String> data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            int shi = 0;
            for (int i = 0; i < data.size(); i++) {
                shi=(Integer.parseInt(data.get(i))  >= 50&&shi==0)?i:shi;
            }
            for (int j = 0; j < data.size(); j++) {
                if (j < shi) {
                    writer.write(data.get(j));
                } else {
                    if (j == shi) {
                        writer.write("-" + String.valueOf(Integer.parseInt(data.get(j)) - 50));
                    } else {
                        writer.write(String.valueOf(Integer.parseInt(data.get(j)) - 50));
                    }
                }
                //writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 组3胆拖保存格式
     * @param file
     * @param data
     */
    public static void saveZu3DantuoNum(File file, List<String> data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            int dan = 0;
            for (int i = 0; i < data.size(); i++) {
                dan=(Integer.valueOf(data.get(i))>=50&&dan==0)?i:dan;
            }
            for (int j = 0; j < data.size(); j++) {
                if (j < dan) {
                    writer.write(data.get(j));
                } else {
                    if (j == dan) {
                        writer.write("," + String.valueOf(Integer.parseInt(data.get(j)) - 50));
                    } else {
                        writer.write(String.valueOf(Integer.parseInt(data.get(j)) - 50));
                    }
                }
                //writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 排列5复式保存格式
     * @param file
     * @param data
     */
    public static void saveP5LocationNum(File file, List<String> data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            int qian = 0;
            int bai = 0;
            int shi = 0;
            int ge = 0;
            for (int i = 0; i < data.size(); i++) {
                qian=(Integer.parseInt(data.get(i))  >= 50&&qian==0)?i:qian;
                bai=(Integer.parseInt(data.get(i))  >= 100&&bai==0)?i:bai;
                shi=(Integer.parseInt(data.get(i))  >= 150&&shi==0)?i:shi;
                ge=(Integer.parseInt(data.get(i))  >= 200&&ge==0)?i:ge;
            }
            for (int j = 0; j < data.size(); j++) {
                if (j < qian) {
                    writer.write(data.get(j));
                } else if (j >= qian && j < bai) {
                    if (j == qian) {
                        writer.write("-" + String.valueOf(Integer.parseInt(data.get(j)) - 50));
                    } else {
                        writer.write(String.valueOf(Integer.parseInt(data.get(j)) - 50));
                    }
                } else if(j >= bai && j < shi){
                    if (j == bai) {
                        writer.write("-" + String.valueOf(Integer.parseInt(data.get(j)) - 100));
                    } else {
                        writer.write(String.valueOf(Integer.parseInt(data.get(j)) - 100));
                    }
                } else if(j >= shi && j < ge){
                    if (j == shi) {
                        writer.write("-" + String.valueOf(Integer.parseInt(data.get(j)) - 150));
                    } else {
                        writer.write(String.valueOf(Integer.parseInt(data.get(j)) - 150));
                    }
                }else{
                    if (j == ge) {
                        writer.write("-" + String.valueOf(Integer.parseInt(data.get(j)) - 200));
                    } else {
                        writer.write(String.valueOf(Integer.parseInt(data.get(j)) - 200));
                    }
                }
                //writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
