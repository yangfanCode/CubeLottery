package com.cp2y.cube.tool.filter;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by js on 2016/12/5.
 * 胆拖过滤器
 */
public class DanTuoFilter implements Filter<byte[]> {

    private Set<Byte> mSums = new HashSet<>();

    public DanTuoFilter(Byte... dans) {
        if (dans != null) {
            for (Byte dan:dans
                 ) {
                mSums.add(dan);
            }
        }
    }

    @Override
    public boolean doFilter(byte[] obj) {
        int len = 0;
        for (byte b: obj
             ) {
            if (mSums.contains(b)) {
                len += 1;
            }
        }
        return len == mSums.size();
    }

}
