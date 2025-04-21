import { useRouter } from "next/router";
import styles from "./styles/Header.module.css";
import Button from "../shared/Button";
import BrandIcon from "@/assets/icon.svg";
import Input from "../shared/Input";
import {
  IoLogInOutline,
  IoLogOutOutline,
  IoPerson,
  IoSearch,
} from "react-icons/io5";
import { logout } from "@/libs/api";
import { useAtom } from "jotai";
import { currentUserAtom } from "@/libs/users/atoms/currentUserAtom";
import { searchTermAtom } from "@/libs/polls/atoms/searchTermAtom";
import { FormEvent, useState } from "react";

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
  const [currentUser, setCurrentUser] = useAtom(currentUserAtom);
  const [searchTerm, setSearchTerm] = useAtom(searchTermAtom);
  const [localSearchTerm, setLocalSearchTerm] = useState(searchTerm);
  const formId = "search-form";

  const handleLogout = async () => {
    try {
      const response = await logout();

      if (response.success) {
        setCurrentUser(null);
        router.push("/home");
      } else {
        throw new Error(response.errorMessage || "Falha ao realizar logout");
      }
    } catch (error) {
      console.error("Logout error:", error);
    }
  };

  const handleSearch = (e: FormEvent) => {
    e.preventDefault();
    setSearchTerm(localSearchTerm);
    
    if (router.pathname !== "/home") {
      router.push("/home");
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
          <Button
            as="Link"
            variant="text"
            key={i}
            id={item.id}
            href={item.href}
          >
            {item.text}
          </Button>
        ))}
        <form id={formId} onSubmit={handleSearch} className="flex w-full cursor-pointer">
          <Input
            id="nav-search-poll"
            className="w-full"
            variant="line"
            placeholder="Pesquisar enquete"
            startElement={
              <div 
                onClick={() => document.getElementById(formId)?.dispatchEvent(new Event('submit', { bubbles: true }))} 
                className="cursor-pointer"
              >
                <IoSearch size={20} />
              </div>
            }
            value={localSearchTerm}
            onChange={(e) => setLocalSearchTerm(e.target.value)}
          />
          <button type="submit" className="hidden">Search</button>
        </form>
      </nav>
      <div className="flex gap-6 text-base font-semibold">
        {currentUser !== null ? (
          <Button
            onClick={handleLogout}
            variant="text"
            scheme="primary"
            id="logout-button"
            className="cursor-pointer"
          >
            <IoLogOutOutline size={20} />
            Logout
          </Button>
        ) : (
          <>
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
            <Button as="Link" id="signup-button" scheme="primary" href="/home">
              <IoPerson size={20} />
              Criar Conta
            </Button>
          </>
        )}
      </div>
    </header>
  );
}
