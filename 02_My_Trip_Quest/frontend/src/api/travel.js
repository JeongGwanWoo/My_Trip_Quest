import axios from 'axios';

const BASE_URL = '/api/v1/tour';

/**
 * 지역별 관광지 조회
 */
export const getAreaBasedList = async (areaCode, cat1, pageNo = 1) => {
  try {
    const params = { pageNo };
    if (areaCode) params.areaCode = areaCode;
    if (cat1) params.cat1 = cat1;

    const response = await axios.get(`${BASE_URL}/area`, { params });
    return response.data;
  } catch (error) {
    console.error('관광지 조회 실패:', error);
    throw error;
  }
};

/**
 * 시군구 목록 조회
 */
export const getSigunguList = async (areaCode) => {
  try {
    const response = await axios.get(`${BASE_URL}/sigungu`, {
      params: { areaCode }
    });
    return response.data;
  } catch (error) {
    console.error('시군구 조회 실패:', error);
    throw error;
  }
};

/**
 * 카테고리 목록 조회
 */
export const getCategoryList = async (cat1, cat2, cat3) => {
  try {
    const response = await axios.get(`${BASE_URL}/category`, {
      params: {
        cat1,
        cat2,
        cat3
      }
    });
    return response.data;
  } catch (error) {
    console.error('카테고리 조회 실패:', error);
    throw error;
  }
};

/**
 * 행사 정보 조회
 */
export const getFestivalList = async (eventStartDate, areaCode, sigunguCode, pageNo = 1) => {
  try {
    const response = await axios.get(`${BASE_URL}/festival`, {
      params: {
        eventStartDate,
        areaCode,
        sigunguCode,
        pageNo
      }
    });
    return response.data;
  } catch (error) {
    console.error('행사 정보 조회 실패:', error);
    throw error;
  }
};
