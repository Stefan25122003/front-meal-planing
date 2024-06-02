import {Header} from "../src/components/Header/Header"
import {Navbar}  from "../src/components/Navbar/Navbar"


export const Layout = ({children, headerImage}) => {
    return(
        <>
            <Navbar />
            <Header headerImage={headerImage}/>
            {
                children
            }
            {/* add footer component */}
        </>
    )
}