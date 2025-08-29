// src/composables/useDate.js
export function useDate() {
  /**
   * UTC 문자열 → KST 문자열
   * @param {string} utcStr "YYYY-MM-DD HH:mm:ss" 형태
   * @returns {string} KST "YYYY-MM-DD HH:mm:ss"
   */
  const utcToKst = (utcStr) => {
    if (!utcStr) return "";

    const [datePart, timePart] = utcStr.split(" ");
    const [y, m, d] = datePart.split("-").map(Number);
    const [h, min, s] = timePart.split(":").map(Number);

    // Date.UTC로 생성
    const date = new Date(Date.UTC(y, m - 1, d, h, min, s));

    // KST 기준 시간 가져오기
    const kstDate = new Date(date.toLocaleString("en-US", { timeZone: "Asia/Seoul" }));

    const YYYY = kstDate.getFullYear();
    const MM = String(kstDate.getMonth() + 1).padStart(2, "0");
    const DD = String(kstDate.getDate()).padStart(2, "0");
    const hh = String(kstDate.getHours()).padStart(2, "0");
    const mm = String(kstDate.getMinutes()).padStart(2, "0");
    const ss = String(kstDate.getSeconds()).padStart(2, "0");

    return `${YYYY}-${MM}-${DD} ${hh}:${mm}:${ss}`;
  };

  return { utcToKst };
}