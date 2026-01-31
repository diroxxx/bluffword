import { useQuery } from "@tanstack/react-query"
import { getCategoryNames } from "../api/getCategoryNames"

export const useCategoryNames = () => {
    return useQuery({
        queryKey: ['categoryNames'],
        queryFn: () => getCategoryNames(),
    })
}