package com.cp2y.cube.utils;

import java.io.File;
import java.util.List;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */

public class CalcCashUtils {
    //计算奖金封装数据

    //双色球大乐透单式
    public static String saveSelectNum(List<String> data) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int j = 0; j < data.size(); j++) {
            int val = Integer.valueOf(data.get(j));
            val = val > 50 ? val - 50 : val;
            if (j == data.size() - 1) {
                stringBuilder.append(CommonUtil.preZeroForBall(val));
            } else {
                stringBuilder.append(CommonUtil.preZeroForBall(val) + " ");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 双色球大乐透复式保存格式
     *
     * @param data
     */
    public static String saveSelectDoubleNum(List<String> data) {
        StringBuilder stringBuilder = new StringBuilder();
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
                    stringBuilder.append(CommonUtil.preZeroForBall(data.get(j)));
                } else {
                    stringBuilder.append(CommonUtil.preZeroForBall(data.get(j)) + " ");
                }
            } else {
                if (j == red) {
                    stringBuilder.append("#" + CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 50)) + " ");
                } else if (j == data.size() - 1) {
                    stringBuilder.append(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 50)));
                } else {
                    stringBuilder.append(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 50)) + " ");
                }

            }
            //writer.newLine();
        }
        return stringBuilder.toString();
    }

    /**
     * 双色球胆拖保存格式
     *
     * @param data
     */
    public static String saveSelectDanTuoNum(List<String> data) {
        StringBuilder stringBuilder = new StringBuilder();
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
                    stringBuilder.append(CommonUtil.preZeroForBall(data.get(j)));
                } else {
                    stringBuilder.append(CommonUtil.preZeroForBall(data.get(j)) + " ");
                }
            } else if (j >= red_dan && j < red_tuo + red_dan) {
                if (j == red_dan) {
                    stringBuilder.append("#" + CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 50)) + " ");
                } else if (j == (red_tuo + red_dan - 1)) {
                    stringBuilder.append(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 50)) + "");
                } else {
                    stringBuilder.append(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 50)) + " ");
                }

            } else {
                if (j == red_tuo + red_dan) {
                    stringBuilder.append("#" + CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 100)) + " ");
                } else if (j == data.size() - 1) {
                    stringBuilder.append(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 100)) + "");
                } else {
                    stringBuilder.append(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 100)) + " ");
                }

            }

            //writer.newLine();
        }
        return stringBuilder.toString();
    }

    /**
     * 大乐透胆拖保存格式
     *
     * @param data
     */
    public static String saveLottoDanTuoNum(List<String> data) {
        StringBuilder stringBuilder = new StringBuilder();
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
                    stringBuilder.append(CommonUtil.preZeroForBall(data.get(j)));
                } else {
                    stringBuilder.append(CommonUtil.preZeroForBall(data.get(j)) + " ");
                }
            } else if (j >= red_dan && j < red_tuo + red_dan) {//红拖
                if (j == red_dan) {
                    stringBuilder.append("#" + CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 50)) + " ");
                } else if (j == (red_tuo + red_dan - 1)) {
                    stringBuilder.append(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 50)) + "");
                } else {
                    stringBuilder.append(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 50)) + " ");
                }

            } else if (j >= red_tuo + red_dan && blue_dan == 0) {//蓝胆
                if (j == red_tuo + red_dan) {//前胆为空
                    stringBuilder.append("# #" + CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 150)) + " ");
                } else if (j == data.size() - 1) {
                    stringBuilder.append(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 150)) + "");
                } else {
                    stringBuilder.append(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 150)) + " ");
                }
            } else if (j >= red_tuo + red_dan && blue_dan != 0) {
                if (blue_dan == 1) {//蓝胆一个没空格
                    if (j == red_tuo + red_dan) {
                        stringBuilder.append("#" + CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 100)) + "");
                    } else if (j >= red_tuo + red_dan + blue_dan) {
                        if (j == red_tuo + red_dan + blue_dan && j == data.size() - 1) {
                            stringBuilder.append("#" + CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 150)) + "");
                        } else if (j == red_tuo + red_dan + blue_dan) {
                            stringBuilder.append("#" + CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 150)) + " ");
                        } else if (j == data.size() - 1) {
                            stringBuilder.append(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 150)) + "");
                        } else {
                            stringBuilder.append(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 150)) + " ");
                        }
                    }
                } else {//蓝拖
                    if (j >= red_tuo + red_dan && j < red_tuo + red_dan + blue_dan) {
                        if (j == red_tuo + red_dan) {
                            stringBuilder.append("#" + CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 100)) + " ");
                        } else if (j == red_tuo + red_dan + blue_dan - 1) {
                            stringBuilder.append(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 100)) + "");
                        } else {
                            stringBuilder.append(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 100)) + " ");
                        }
                        stringBuilder.append(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 100)) + "");
                    } else if (j >= red_tuo + red_dan + blue_dan) {
                        if (j == red_tuo + red_dan + blue_dan) {
                            stringBuilder.append("#" + CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 150)) + " ");
                        } else if (j == data.size() - 1) {
                            stringBuilder.append(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 150)) + "");
                        } else {
                            stringBuilder.append(CommonUtil.preZeroForBall(String.valueOf(Integer.parseInt(data.get(j)) - 150)) + " ");
                        }
                    }
                }
            }
            //writer.newLine();
        }
        return stringBuilder.toString();
    }

    /**
     * 福彩3D单式保存格式
     *
     * @param data
     */
    public static String save3dNum(List<String> data) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int j = 0; j < data.size(); j++) {
            stringBuilder.append(data.get(j));
            //writer.newLine();
        }
        return stringBuilder.toString();
    }

    /**
     * 福彩3D直选定位保存格式
     *
     * @param data
     */
    public static String saveD3LocationNum(List<String> data) {
        StringBuilder stringBuilder = new StringBuilder();
        int bai = 0;
        int shi = 0;
        for (int i = 0; i < data.size(); i++) {
            bai = (Integer.parseInt(data.get(i)) >= 50 && bai == 0) ? i : bai;
            shi = (Integer.parseInt(data.get(i)) >= 100 && shi == 0) ? i : shi;
        }
        for (int j = 0; j < data.size(); j++) {
            if (j < bai) {
                stringBuilder.append(data.get(j));
            } else if (j >= bai && j < shi) {
                if (j == bai) {
                    stringBuilder.append("-" + String.valueOf(Integer.parseInt(data.get(j)) - 50));
                } else {
                    stringBuilder.append(String.valueOf(Integer.parseInt(data.get(j)) - 50));
                }
            } else {
                if (j == shi) {
                    stringBuilder.append("-" + String.valueOf(Integer.parseInt(data.get(j)) - 100));
                } else {
                    stringBuilder.append(String.valueOf(Integer.parseInt(data.get(j)) - 100));
                }
            }
            //writer.newLine();
        }
        return stringBuilder.toString();
    }

    /**
     * 组3胆拖保存格式
     * @param data
     */
    public static String saveZu3DantuoNum(List<String> data) {
        StringBuilder stringBuilder = new StringBuilder();
            int dan = 0;
            for (int i = 0; i < data.size(); i++) {
                dan=(Integer.valueOf(data.get(i))>=50&&dan==0)?i:dan;
            }
            for (int j = 0; j < data.size(); j++) {
                if (j < dan) {
                   stringBuilder.append(data.get(j));
                } else {
                    if (j == dan) {
                       stringBuilder.append("," + String.valueOf(Integer.parseInt(data.get(j)) - 50));
                    } else {
                       stringBuilder.append(String.valueOf(Integer.parseInt(data.get(j)) - 50));
                    }
                }
                //writer.newLine();
            }
            return stringBuilder.toString();
    }

    /**
     * 排列5复式保存格式
     * @param file
     * @param data
     */
    public static String saveP5LocationNum(List<String> data) {
        StringBuilder stringBuilder = new StringBuilder();
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
                    stringBuilder.append(data.get(j));
                } else if (j >= qian && j < bai) {
                    if (j == qian) {
                        stringBuilder.append("-" + String.valueOf(Integer.parseInt(data.get(j)) - 50));
                    } else {
                        stringBuilder.append(String.valueOf(Integer.parseInt(data.get(j)) - 50));
                    }
                } else if(j >= bai && j < shi){
                    if (j == bai) {
                        stringBuilder.append("-" + String.valueOf(Integer.parseInt(data.get(j)) - 100));
                    } else {
                        stringBuilder.append(String.valueOf(Integer.parseInt(data.get(j)) - 100));
                    }
                } else if(j >= shi && j < ge){
                    if (j == shi) {
                        stringBuilder.append("-" + String.valueOf(Integer.parseInt(data.get(j)) - 150));
                    } else {
                        stringBuilder.append(String.valueOf(Integer.parseInt(data.get(j)) - 150));
                    }
                }else{
                    if (j == ge) {
                        stringBuilder.append("-" + String.valueOf(Integer.parseInt(data.get(j)) - 200));
                    } else {
                        stringBuilder.append(String.valueOf(Integer.parseInt(data.get(j)) - 200));
                    }
                }
                //writer.newLine();
            }
            return stringBuilder.toString();
    }
}
