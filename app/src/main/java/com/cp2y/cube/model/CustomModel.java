package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2017/8/14.
 */
public class CustomModel {
	private List<Detail> lotteryCustom;

	private List<Detail> lotteryCustomAZ;

	public List<Detail> getLotteryCustom() {
		return lotteryCustom;
	}

	public List<Detail> getLotteryCustomAZ() {
		return lotteryCustomAZ;
	}

	public void setLotteryCustomAZ(List<Detail> lotteryCustomAZ) {
		this.lotteryCustomAZ = lotteryCustomAZ;
	}

	public void setLotteryCustom(List<Detail> lotteryCustom) {
		this.lotteryCustom = lotteryCustom;
	}

	public static class Detail {
		private int lotteryID;
		private String lotteryName;

		public Detail(int lotteryID, String lotteryName) {
			this.lotteryID = lotteryID;
			this.lotteryName = lotteryName;
		}

		public Detail() {
		}

		public int getLotteryID() {
			return lotteryID;
		}

		public void setLotteryID(int lotteryID) {
			this.lotteryID = lotteryID;
		}

		public String getLotteryName() {
			return lotteryName;
		}

		public void setLotteryName(String lotteryName) {
			this.lotteryName = lotteryName;
		}
	}
}
