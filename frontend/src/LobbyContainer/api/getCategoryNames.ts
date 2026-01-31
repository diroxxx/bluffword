import customAxios  from "../../lib/customAxios.ts";

export const getCategoryNames = async () => {
    const result = await customAxios.get<string[]>('/api/categories');
    console.log("Fetched categories:", result.data);
    return result.data;
}
