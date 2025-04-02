import Main from "@/components/base/Main";
import "../styles/globals.css";
import type { AppProps } from "next/app";
import Header from "@/components/base/Header";
import Footer from "@/components/base/Footer";
import Head from "next/head";

export default function MyApp({ Component, pageProps }: AppProps) {
  return (
    <>
      <Head>
        <link rel="icon" href="/icon.svg" type="image/svg+xml" />
      </Head>
      <Header />
      <Main>
        <Component {...pageProps} />
      </Main>
      <Footer />
    </>
  );
}
