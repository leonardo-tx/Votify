import Link, { LinkProps } from "next/link";
import {
  AnchorHTMLAttributes,
  ButtonHTMLAttributes,
  forwardRef,
  ReactNode,
  Ref,
} from "react";
import styles from "./styles/Button.module.css";

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  as?: "button";
};

type AnchorProps = AnchorHTMLAttributes<HTMLAnchorElement> & {
  as: "a";
};

type NextLinkProps = {
  as: "Link";
  href: LinkProps["href"];
} & Omit<AnchorHTMLAttributes<HTMLAnchorElement>, "href">;

type Props = {
  variant?: "solid" | "outline" | "text";
  scheme?: "primary";
  className?: string;
  children?: ReactNode;
} & (ButtonProps | AnchorProps | NextLinkProps);

const Button = forwardRef<HTMLButtonElement | HTMLAnchorElement, Props>(
  (
    { as = "button", variant = "solid", scheme, className, children, ...props },
    ref,
  ) => {
    const defaultClassNames =
      styles["common"] +
      " " +
      styles[variant] +
      (scheme === undefined ? "" : " " + styles[scheme + "-" + variant]);

    switch (as) {
      case "button":
        props = props as ButtonProps;
        return (
          <button
            ref={ref as Ref<HTMLButtonElement>}
            draggable="false"
            className={defaultClassNames + " " + className}
            {...props}
          >
            {children}
          </button>
        );
      case "a":
        props = props as AnchorProps;
        return (
          <a
            ref={ref as Ref<HTMLAnchorElement>}
            draggable="false"
            className={defaultClassNames + " " + className}
            {...props}
          >
            {children}
          </a>
        );
      case "Link":
        props = props as LinkProps;
        return (
          <Link
            ref={ref as Ref<HTMLAnchorElement>}
            draggable="false"
            className={defaultClassNames + " " + className}
            {...props}
          >
            {children}
          </Link>
        );
      default:
        const _exhaustiveCheck: never = as;
        return _exhaustiveCheck;
    }
  },
);

Button.displayName = "Button";
export default Button;
