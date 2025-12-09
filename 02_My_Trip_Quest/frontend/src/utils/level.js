// src/utils/level.js

const XP_PER_LEVEL = 1000;

/**
 * 총 경험치를 기반으로 현재 레벨을 계산합니다.
 * @param {number} totalXp - 총 경험치
 * @returns {number} 현재 레벨
 */
export const calculateLevel = (totalXp) => {
  if (totalXp === undefined || totalXp === null || totalXp < 0) return 1;
  return 1 + Math.floor(totalXp / XP_PER_LEVEL);
};

/**
 * 주어진 레벨에 도달하기 위해 필요한 총 경험치를 계산합니다.
 * @param {number} level - 목표 레벨
 * @returns {number} 필요한 총 경험치
 */
export const getXpForLevel = (level) => {
  if (level <= 1) return 0;
  return (level - 1) * XP_PER_LEVEL;
};

/**
 * 총 경험치를 기반으로 레벨 진행률에 대한 상세 정보를 계산합니다.
 * @param {number} totalXp - 총 경험치
 * @returns {object} 레벨 상세 정보 객체
 */
export const getLevelProgress = (totalXp) => {
  const currentLevel = calculateLevel(totalXp);
  const xpForCurrentLevel = getXpForLevel(currentLevel);
  const xpForNextLevel = getXpForLevel(currentLevel + 1);

  const xpInCurrentLevel = totalXp - xpForCurrentLevel;
  const xpNeededForLevelUp = xpForNextLevel - xpForCurrentLevel;

  const progressPercentage = xpNeededForLevelUp > 0 ? (xpInCurrentLevel / xpNeededForLevelUp) * 100 : 0;
  
  return {
    currentLevel,
    xpForCurrentLevel,
    xpForNextLevel,
    xpInCurrentLevel,
    xpNeededForLevelUp,
    progressPercentage: Math.floor(progressPercentage), // 소수점 버림
  };
};
