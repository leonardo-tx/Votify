import { useState, useEffect } from "react";
import { useRouter } from "next/router";
import styles from "./styles/Header.module.css";
import Button from "../shared/Button";
import BrandIcon from "@/assets/icon.svg";
import Input from "../shared/Input";
import { IoLogInOutline, IoPerson, IoSearch } from "react-icons/io5";
import { getCurrentUser, logout } from "@/libs/api";

interface NavItem {
  text: string;
  id: string;
  href: string;
}

const navItems: NavItem[] = [
  { id: "nav-about-anchor", text: "Sobre nós", href: "/home" },
];

export default function Header() {
  const router = useRouter();
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  const checkAuth = async () => {
    try {
      const response = await getCurrentUser();
      setIsLoggedIn(response.success);
    } catch {
      setIsLoggedIn(false);
    }
  };

  useEffect(() => {
    checkAuth();
    router.events.on("routeChangeComplete", checkAuth);
    return () => {
      router.events.off("routeChangeComplete", checkAuth);
    };
  }, [router.events]);

  const handleLogout = async () => {
    try {
      const response = await logout();

      if (response.success) {
        setIsLoggedIn(false);
        router.push("/home");
      } else {
        throw new Error(response.errorMessage || "Falha ao realizar logout");
      }
    } catch (error) {
      console.error("Logout error:", error);
    }
  };

  return (
    <header className={styles.header}>
      <Button
        as="Link"
        variant="text"
        scheme="primary"
        id="logo-home-anchor"
        className="text-3xl font-semibold tracking-wide"
        href="/home"
      >
        <BrandIcon height={40} width={40} />
      </Button>
      <nav className="flex items-center gap-6 text-md font-semibold">
        {navItems.map((item, i) => (
          <Button as="Link" variant="text" key={i} id={item.id} href={item.href}>
            {item.text}
          </Button>
        ))}
        <Input
          id="nav-search-poll"
          className="w-full"
          variant="line"
          placeholder="Pesquisar enquete"
          startElement={<IoSearch size={20} />}
        />
      </nav>
      <div className="flex gap-6 text-base font-semibold">
        {isLoggedIn ? (
          <Button
            onClick={handleLogout}
            variant="outline"
            scheme="primary"
            id="logout-button"
            className="cursor-pointer"
          >
            <IoLogInOutline size={20} />
            Logout
          </Button>
        ) : (
          <Button
            as="Link"
            variant="outline"
            scheme="primary"
            id="login-button"
            href="/login"
          >
            <IoLogInOutline size={20} />
            Entrar
          </Button>
        )}
        {!isLoggedIn && (
          <Button 
            as="Link" 
            id="signup-button" 
            scheme="primary" 
            href="/home"
          >
            <IoPerson size={20} />
            Criar Conta
          </Button>
        )}
      </div>
    </header>
  );
}