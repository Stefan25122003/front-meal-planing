import { Layout } from "../Layout"
import RecipeCards from "../RecipeCards";
import headerBackground from "../Photos/header-background.png";
import AIPart from "../AIPart";

export const Home = () => {

    return (
        <Layout headerImage={headerBackground}>
             <AIPart />
            <RecipeCards />
        </Layout>
    )
}
