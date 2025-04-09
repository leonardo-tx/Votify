import Main from "@/components/base/Main";
import "../styles/globals.css";
import type { AppProps } from "next/app";
import Header from "@/components/base/Header";
import Footer from "@/components/base/Footer";
import Head from "next/head";
import { Provider } from "jotai";
import GlobalValuesProvider from "@/components/base/GlobalValuesProvider";

export default function MyApp({ Component, pageProps }: AppProps) {
  return (
    <Provider>
      <GlobalValuesProvider>
        <Head>
          <link rel="icon" href="/icon.svg" type="image/svg+xml" />
        </Head>
        <Header />
        <Main>
          <Component {...pageProps} />
        </Main>
        <Footer />
      </GlobalValuesProvider>
    </Provider>
  );
}
